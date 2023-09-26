package com.example.viewdebug.xml.struct

import android.content.Context
import com.example.viewdebug.xml.struct.writer.AndroidAttributeConvertor
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.AttributeConvertor
import com.example.viewdebug.xml.struct.writer.ChunkEndTagWriter
import com.example.viewdebug.xml.struct.writer.ChunkFileWriter
import com.example.viewdebug.xml.struct.writer.ChunkNamespaceWriter
import com.example.viewdebug.xml.struct.writer.ChunkStartTagWriter
import com.example.viewdebug.xml.struct.writer.ChunkStringWriter
import com.example.viewdebug.xml.struct.writer.DefaultAttributeConvertor
import com.example.viewdebug.xml.struct.writer.helper.AttributeWriterHelper
import com.example.viewdebug.xml.struct.writer.link.ResourceLink
import com.example.viewdebug.xml.struct.writer.link.ResourceLinkImpl
import com.skin.log.Logger
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.LinkedList
import javax.xml.parsers.DocumentBuilderFactory

/**
 * xml 转 axml
 * 在解析时就应该创建axml结构
 */
class XmlCompiler(private val ctx: Context) {
    private val chunkFile = ChunkFileWriter()
    private val resourceLink: ResourceLink = ResourceLinkImpl(ctx)
    private val attributeWriterHelper = AttributeWriterHelper(this)
    private val factory = DocumentBuilderFactory.newInstance()
    private val builder = factory.newDocumentBuilder()
    // 属性转换列表，这些属性需要转换，来在低版本上进行生效
    // 比如，遇到paddingHorizontal属性，需要添加paddingLeft和paddingRight属性
    private val attributeConvertorList = LinkedList<AttributeConvertor>().apply {
        add(AndroidAttributeConvertor("paddingHorizontal",  arrayOf("paddingLeft", "paddingRight"), true))
        add(AndroidAttributeConvertor("paddingVertical",  arrayOf("paddingTop", "paddingBottom"), true))
        add(AndroidAttributeConvertor("layout_marginHorizontal",  arrayOf("layout_marginLeft", "layout_marginRight"), true))
        add(AndroidAttributeConvertor("layout_marginVertical",  arrayOf("layout_marginTop", "layout_marginBottom"), true))
    }

    private var filterTextNode = true

    // 申请1024*100的空间
    private val buffer: ByteBuffer = ByteBuffer.allocate(1024 * 100).order(ByteOrder.LITTLE_ENDIAN)
    fun compile(stream: InputStream): ByteBuffer {
        val doc = builder.parse(stream)
        // step 1：创建字符常量池
        // 记录所有的node name、attribute name、attribute value、namespace
        //compile(doc.childNodes, buffer, true)
        compile(doc.childNodes, buffer)
        chunkFile.write(buffer)
        buffer.limit(chunkFile.chunkSize)
        buffer.position(0)
        return buffer
    }

    private fun compile(nodes: NodeList, buffer: ByteBuffer) {
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
            addStartTag(node)
            compile(node.childNodes, buffer)
            addEndTag(node)
        }
    }

    /**
     * 字符串常量也需要排序
     * 按照R.attr.xxx从小到大排序
     *
     * 所以这里需要重构，先添加所有字符信息，待write时才根据字符串确定index
     */
    private fun addString(string: String, type: Int, priority: Int) {
        chunkFile.chunkString.addString(type, priority, string)
    }

    private fun addAttrNameString(attrName: String, nsPrefix: String) {
        val priority = resourceLink.getAttributeId(nsPrefix, attrName) ?: Int.MAX_VALUE
        addString(attrName, ChunkStringWriter.PRIORITY_TYPE_ATTR_NAME, priority)
    }

    fun addOtherString(string: String) {
        if (string.isEmpty()) return
        addString(string, ChunkStringWriter.PRIORITY_TYPE_ATTR_VALUE, 0)
    }

    private fun addNamespace(node: Node) {
        val prefix = node.nodeName.substring("xmlns:".length)
        if (prefix == "tools") return
        val uri = node.nodeValue
        addOtherString(uri)
        addOtherString(prefix)
        chunkFile.chunkStartNamespace.add(ChunkNamespaceWriter(0, ChunkNamespaceWriter.TYPE_START, chunkFile).apply {
            this.comment = -1
            this.uri = uri
            this.lineNumber = 0
            this.prefix = prefix
        })
        chunkFile.chunkEndNamespace.add(ChunkNamespaceWriter(0, ChunkNamespaceWriter.TYPE_END, chunkFile).apply {
            this.comment = -1
            this.uri = uri
            this.lineNumber = 0
            this.prefix = prefix
        })

        // 这里将命名空间提升到顶级， todo 确定命名空间实际位置
    }

    private fun addAttribute(tagNode: Node, attr: Node) {
        if (attr.nodeName.startsWith("tools:")) return

        if (attr.nodeName.contains(":")) {
            val attrSplit = attr.nodeName.split(":")
            addAttribute(tagNode.nodeName, attrSplit[0], attrSplit[1], attr.nodeValue)
        } else {
            addAttribute(tagNode.nodeName, "", attr.nodeName, attr.nodeValue)
        }
    }

    /**
     * 添加属性解析
     * @param tagName 标签名称
     * @param prefix 前缀
     * @param attributeName 属性名称
     * @param value 属性值
     * @param overwrite 是否覆盖已经有的属性
     */
    fun addAttribute(tagName: String, prefix: String, attributeName: String, value: String, overwrite: Boolean = false) {
        if (prefix.isNotEmpty()) {
            addOtherString(prefix)
            addAttrNameString(attributeName, prefix)
        } else {
            addOtherString(attributeName)
        }

        // 忽略tools
        val tag = (chunkFile.chunkTags.last as ChunkStartTagWriter)
        val existAttr = tag.attributes.find { it.name == attributeName && it.namespacePrefix == prefix }
        if (existAttr != null) {
            if (overwrite) {
                // 覆盖属性，需要移除已有
                tag.attributes.remove(existAttr)
            } else {
                // 不覆盖，直接忽略当前添加
                return
            }
        }
        val attr = Attribute(Int.MAX_VALUE, chunkFile).apply {
            if (prefix.isNotEmpty()) {
                this.name = attributeName
                this.namespacePrefix = prefix

                // 设置 用于排序
                this.systemResourceId = resourceLink.getAttributeId(prefix, attributeName)!!
                chunkFile.chunkSystemResourceId.resourceIds.add(systemResourceId)
            } else {
                this.name = attributeName
                this.namespacePrefix = ""
            }
            // 从常量池获取，因为目前所有值都在这里
            val resValue = attributeWriterHelper.compileAttributeResValue(tagName, attributeName, value, prefix) ?: throw Exception("无法解析：${tagName} $prefix $attributeName ${value}")
            this.resValue = resValue
            Logger.v("sssss", resValue.data.toString() + "  ${prefix}:${attributeName}  $value")
        }
        tag.attributes.add(attr)
    }

    private fun addStartTag(node: Node) {
        addOtherString(node.nodeName)
        val tag = ChunkStartTagWriter(0, chunkFile)
        chunkFile.chunkTags.add(tag)
        tag.apply {
            this.name = node.nodeName
            // tag 暂时不算命名空间
            this.namespaceUri = ""
            this.comment = -1
            // this.idIndex = 0
            /*this.classIndex = 0
            this.styleIndex = 0*/
            if (node.hasAttributes()) {
                this.attrCount = node.attributes.length.toShort()

                val attrs = ArrayList<Node>()
                for (i in 0 until node.attributes.length) {
                    val attr = node.attributes.item(i)
                    // 这里是命名空间
                    if (attr.nodeName.startsWith("xmlns:")) {
                        addNamespace(attr)
                    } else {
                        attrs.add(attr)
                        // addAttribute(attr, findStr)
                    }
                }
                attrs.forEach {
                    addAttribute(node, it)
                }
                // 适配特殊属性
                fixSpecialAttribute(node)
            }

        }
    }


    private fun addEndTag(node: Node) {
        chunkFile.chunkTags.add(
            ChunkEndTagWriter(0, chunkFile).apply {
                this.name = node.nodeName
                // tag 暂时不算命名空间
                this.namespaceUri = ""
                this.comment = -1
            },
        )
    }

    /**
     * 适配特殊属性
     */
    private fun fixSpecialAttribute(tagNode: Node) {
        attributeConvertorList.forEach {
            it.onParsed(tagNode, this)
        }
        return
        val tag = (chunkFile.chunkTags.last as ChunkStartTagWriter)
        val ph = tag.attributes.find { it.name == "paddingHorizontal" && it.namespacePrefix == "android" }
        if (ph != null) {
            // 适配paddingHorizontal
            val phNode = tagNode.attributes.getNamedItem("android:paddingHorizontal")
            if (tag.attributes.find { it.name == "paddingLeft" && it.namespacePrefix == "android" } == null) {
                addAttribute(tag.name, "android", "paddingLeft", phNode.nodeValue)
            }
            if (tag.attributes.find { it.name == "paddingRight" && it.namespacePrefix == "android" } == null) {
                addAttribute(tag.name, "android", "paddingRight", phNode.nodeValue)
            }
        }
        val pv = tag.attributes.find { it.name == "paddingVertical" && it.namespacePrefix == "android" }
        if (pv != null) {
            // 适配 paddingVertical
            val pvNode = tagNode.attributes.getNamedItem("android:paddingVertical")
            if (tag.attributes.find { it.name == "paddingTop" && it.namespacePrefix == "android" } == null) {
                addAttribute(tag.name, "android", "paddingTop", pvNode.nodeValue)
            }
            if (tag.attributes.find { it.name == "paddingBottom" && it.namespacePrefix == "android" } == null) {
                addAttribute(tag.name, "android", "paddingBottom", pvNode.nodeValue)
            }
        }

        val mh = tag.attributes.find { it.name == "layout_marginHorizontal" && it.namespacePrefix == "android" }
        if (mh != null) {
            // 适配layout_marginHorizontal
            val msNode = tagNode.attributes.getNamedItem("android:layout_marginHorizontal")
            if (tag.attributes.find { it.name =="layout_marginStart" && it.namespacePrefix == "android" } == null) {
                addAttribute(tag.name, "android", "marginStart", msNode.nodeValue)
            }
        }

    }
}
