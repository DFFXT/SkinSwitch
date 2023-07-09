package com.example.viewdebug.xml.struct

import android.content.Context
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.ChunkEndTagWriter
import com.example.viewdebug.xml.struct.writer.ChunkFileWriter
import com.example.viewdebug.xml.struct.writer.ChunkNamespaceWriter
import com.example.viewdebug.xml.struct.writer.ChunkStartTagWriter
import com.example.viewdebug.xml.struct.writer.ChunkStringWriter
import com.example.viewdebug.xml.struct.writer.helper.AttributeWriterHelper
import com.skin.log.Logger
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * xml 转 axml
 * 在解析时就应该创建axml结构
 */
class XmlCompiler(private val ctx: Context) {
    private val chunkFile = ChunkFileWriter()
    private val attributeWriterHelper = AttributeWriterHelper(this)
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()


    // key 是字符串，value是字节偏移
    private val stringPool = LinkedHashMap<String, Pair<Int, Int>>()
    private var poolByteSize = 0
    private var filterTextNode = true
    // 申请1024*100的空间
    private val buffer: ByteBuffer = ByteBuffer.allocate(1024 * 100).order(ByteOrder.LITTLE_ENDIAN)
    fun compile(stream: InputStream): ByteBuffer {
        addString("id")
        addString("background")
        addString("layout_width")
        addString("layout_height")
        addString("View")
        addString("android")
        addString("androidx.constraintlayout.widget.ConstraintLayout")
        addString("http://schemas.android.com/apk/res/android")
        val doc = builder.parse(stream)
        // step 1：创建字符常量池
        // 记录所有的node name、attribute name、attribute value、namespace
        val len = doc.childNodes.length
        compile(doc.childNodes, buffer, true)
        compile(doc.childNodes, buffer, false)
        chunkFile.write(buffer)
        buffer.limit(chunkFile.chunkSize)
        buffer.position(0)
        return buffer
    }

    private fun compile(nodes: NodeList, buffer: ByteBuffer, findStr: Boolean) {
        val len = nodes.length
        for (i in 0 until len) {
            val node = nodes.item(i)
            if (filterTextNode && node.nodeType == Node.TEXT_NODE) {
                // 过滤text节点
                continue
            }
            if (node.nodeType == Node.COMMENT_NODE) {
                continue
            }
            addStartTag(node, findStr)
            compile(node.childNodes, buffer, findStr)
            /*if (node.hasAttributes()) {
                val attrs = node.attributes
                for (attrIndex in 0 until attrs.length) {
                    val attr = attrs.item(attrIndex)
                    // 这里是命名空间
                    if (attr.nodeName.startsWith("xmlns:")) {
                        addNamespace(attr, findStr)
                    } else {
                        addAttribute(attr, findStr)
                    }
                }
            }*/
            addEndTag(node, findStr)
        }
    }

    /**
     * 字符串常量也需要排序
     * 按照R.attr.xxx从小到大排序
     *
     * 所以这里需要重构，先添加所有字符信息，待write时才根据字符串确定index
     */
    fun addString(string: String, priority: Int = 0): Int {
        val preByteSize = poolByteSize
        if (!stringPool.contains(string)) {
            stringPool[string] = Pair(stringPool.size, poolByteSize)
            chunkFile.chunkString.stringOffsets.add(poolByteSize)
            chunkFile.chunkString.stringPools[poolByteSize] = ChunkStringWriter.StringPool(string)
            poolByteSize += string.toByteArray().size + ChunkStringWriter.StringPool.extra_size_utf8
            println("add:->" + string)
        }

        return chunkFile.chunkString.stringOffsets.indexOf(preByteSize)
    }

    fun getStringIndex(str: String): Int {
        println(str)
        return stringPool[str]!!.first
    }

    private fun addNamespace(node: Node, findStr: Boolean) {
        val prefix = node.nodeName.substring("xmlns:".length)
        val uri = node.nodeValue
        if (findStr) {
            addString(uri)
            addString(prefix)
        } else {
            val index = getStringIndex(uri)
            chunkFile.chunkStartNamespace[index] = ChunkNamespaceWriter(0, ChunkNamespaceWriter.TYPE_START).apply {
                this.comment = -1
                this.uri = index
                this.lineNumber = 0
                this.prefix = getStringIndex(prefix)
            }
            chunkFile.chunkEndNamespace[index] = ChunkNamespaceWriter(0, ChunkNamespaceWriter.TYPE_END).apply {
                this.comment = -1
                this.uri = index
                this.lineNumber = 0
                this.prefix = getStringIndex(prefix)
            }
        }
        // 这里将命名空间提升到顶级， todo 确定命名空间实际位置
    }

    private fun addAttribute(tagNode: Node, attr: Node, findStr: Boolean) {
        if (findStr) {
            // 这里需要判断是否是引用类型

            if (attr.nodeName.contains(":")) {
                val attrSplit = attr.nodeName.split(":")
                addString(attrSplit[1])
                addString(attrSplit[0])
                //addString(attr.nodeValue)
            } else {
                addString(attr.nodeName)
            }
        } else {
            // 忽略tools
            if (attr.nodeName.startsWith("tools:")) return
            val tag = (chunkFile.chunkTags.last as ChunkStartTagWriter)
            tag.attributes.add(
                Attribute(Int.MAX_VALUE).apply {
                    var nsPrefix: String? = null
                    var attrName: String
                    if (attr.nodeName.contains(":")) {
                        val attrSplit = attr.nodeName.split(":")
                        attrName = attrSplit[1]
                        this.name = getStringIndex(attrName)
                        nsPrefix = attrSplit[0]
                        val prefixIndex = getStringIndex(nsPrefix)
                        var uri = 0
                        for (ns in chunkFile.chunkStartNamespace) {
                            if (ns.value.prefix == prefixIndex) {
                                uri = ns.value.uri
                                break
                            }
                        }
                        this.namespaceUri = uri
                        // 当前是id，添加id资源
                        if (attr.nodeName == "android:id") {
                            addViewId(attr.nodeValue)
                            tag.idIndex = ((tag.attributes.size.toShort() + 1).toShort())
                        }
                        // 设置 用于排序
                        this.systemResourceId = if (nsPrefix == "android") {
                            ViewDebugInitializer.ctx.resources.getIdentifier(attrName,"attr", "android")
                        } else {
                            ViewDebugInitializer.ctx.resources.getIdentifier(attrName,"attr", ViewDebugInitializer.ctx.packageName)
                        }
                    } else {
                        attrName = attr.nodeName
                        this.name = getStringIndex(attrName)
                        this.namespaceUri = -1
                    }
                    // 从常量池获取，因为目前所有值都在这里
                    val resValue = attributeWriterHelper.compileAttributeResValue(tagNode.nodeName, attrName, attr.nodeValue, nsPrefix)!!
                    val pkg = if (nsPrefix == "android") {
                        "android"
                    } else {
                        ctx.packageName
                    }
                    val attrId = ctx.resources.getIdentifier(attrName, "attr", pkg)
                    Logger.i("++++", "id = " + attrId)
                    chunkFile.chunkSystemResourceId.resourceIds.add(attrId)
                    // resValue.type
                    this.value = if (!resValue.parentValue) -1 else resValue.data
                    this.resValue = resValue
                    Logger.v("sssss", resValue.data.toString() +"  ${attr.nodeName}  ${attr.nodeValue}")


                },
            )
        }
    }

    private fun addViewId(attrValue: String) {

    }

    private fun addStartTag(node: Node, findStr: Boolean) {
        if (findStr) {
            addString(node.nodeName)
            if (node.hasAttributes()) {
                val attrs = ArrayList<Node>()
                for (i in 0 until node.attributes.length) {
                    val attr = node.attributes.item(i)
                    // 忽略tools
                    if (attr.nodeName.startsWith("tools:")) {
                        continue
                    }
                    // 这里是命名空间
                    if (attr.nodeName.startsWith("xmlns:")) {
                        addNamespace(attr, findStr)
                    } else {
                        attrs.add(attr)
                        // addAttribute(attr, findStr)
                    }
                }
                attrs.forEach {
                    addAttribute(node, it, findStr)
                }
            }
        } else {
            val tag = ChunkStartTagWriter(0)
            chunkFile.chunkTags.add(tag)
            tag.apply {
                this.name = getStringIndex(node.nodeName)
                // tag 暂时不算命名空间
                this.namespaceUri = -1
                this.comment = -1
                this.idIndex = 0
                this.classIndex = 0
                this.styleIndex = 0
                if (node.hasAttributes()) {
                    this.attrCount = node.attributes.length.toShort()

                    val attrs = ArrayList<Node>()
                    for (i in 0 until node.attributes.length) {
                        val attr = node.attributes.item(i)
                        // 这里是命名空间
                        if (attr.nodeName.startsWith("xmlns:")) {
                            addNamespace(attr, findStr)
                        } else {
                            attrs.add(attr)
                            // addAttribute(attr, findStr)
                        }
                    }
                    attrs.forEach {
                        addAttribute(node, it, findStr)
                    }
                    /*for (i in 0 until node.attributes.length) {
                        val attr = node.attributes.item(i)
                        // 这里是命名空间
                        if (attr.nodeName.startsWith("xmlns:")) {
                            addNamespace(attr, findStr)
                        } else {
                            addAttribute(attr, findStr)
                        }

                    }*/
                }
            }
        }
    }

    private fun addEndTag(node: Node, findStr: Boolean) {
        if (findStr) {
            addString(node.nodeName)
        } else {
            chunkFile.chunkTags.add(
                ChunkEndTagWriter(0).apply {
                    this.name = getStringIndex(node.nodeName)
                    // tag 暂时不算命名空间
                    this.namespaceUri = -1
                    this.comment = -1
                },
            )
        }
    }
}