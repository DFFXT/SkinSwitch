package com.example.viewdebug.xml.struct.reader

import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and

/**
 * android xml格式是采用的小端存储
 * java 默认是大端存储，所以也是大端读取
 * 只有在多个字节的读取时才有区别，单个字节读取没有大小端的区别
 * 人在读取16进制数据时都是默认小端存储，即低位在右侧，大端是高位在右侧
 * 大端读取，从低位开始读，
 * 比如两个字节的short：4460,其二进制为0x1234,byteBuffer存储为[12,34]
 * 如果是小端方式读取(byteBuffer.getShort())，则读取正常，取值为0x1234=4460
 * 如果是大端端方式读取(byteBuffer.getShort())，则读取错误，取值为0x3412=13330
 * 所以，在读取多byte时，需确定存储方式，然后才能正确读取
 *
 */

/**
 * 6    RES_NULL_TYPE               = 0x0000,
217    RES_STRING_POOL_TYPE        = 0x0001,
218    RES_TABLE_TYPE              = 0x0002,
219    RES_XML_TYPE                = 0x0003,
220
221    // Chunk types in RES_XML_TYPE
222    RES_XML_FIRST_CHUNK_TYPE    = 0x0100,
223    RES_XML_START_NAMESPACE_TYPE= 0x0100,
224    RES_XML_END_NAMESPACE_TYPE  = 0x0101,
225    RES_XML_START_ELEMENT_TYPE  = 0x0102,
226    RES_XML_END_ELEMENT_TYPE    = 0x0103,
227    RES_XML_CDATA_TYPE          = 0x0104,
228    RES_XML_LAST_CHUNK_TYPE     = 0x017f,
229    // This contains a uint32_t array mapping strings in the string
230    // pool back to resource identifiers.  It is optional.
231    RES_XML_RESOURCE_MAP_TYPE   = 0x0180,
232
233    // Chunk types in RES_TABLE_TYPE
234    RES_TABLE_PACKAGE_TYPE      = 0x0200,
235    RES_TABLE_TYPE_TYPE         = 0x0201,
236    RES_TABLE_TYPE_SPEC_TYPE    = 0x0202,
237    RES_TABLE_LIBRARY_TYPE      = 0x0203
 */

abstract class BaseChunk : IRead {
    var type: Short = 0
    var headerSize: Short = 0
    var chunkSize: Int = 0
    override fun read(data: ByteBuffer) {
        type = data.short
        headerSize = data.short
        chunkSize = data.int
        onRead(data)
    }

    protected abstract fun onRead(data: ByteBuffer)
}

/**
 * tag 基类
 */
abstract class BaseTagChunk : BaseChunk() {
    var lineNumber: Int = 0
    var comment: Int = 0
    var namespaceUri: Int = 0
    var name: Int = 0
    override fun onRead(data: ByteBuffer) {
        lineNumber = data.int
        comment = data.int
        namespaceUri = data.int
        name = data.int
        onRead2(data)
    }

    abstract fun onRead2(data: ByteBuffer)
}

class ChunkFile : BaseChunk() {
    companion object {
        const val HEADER_SIZE = 0x8
    }

    // 字符串常量池
    val chunkString: ChunkString = ChunkString()
    val chunkSystemResourceId: ChunkSystemResourceId = ChunkSystemResourceId()
    var chunkStartNamespace = HashMap<Int, ChunkNamespace>()
    var chunkEndNamespace = HashMap<Int, ChunkNamespace>()
    val chunkTags = LinkedList<BaseTagChunk>()
    override fun onRead(data: ByteBuffer) {
        chunkString.read(data)
        data.position(chunkString.chunkSize + headerSize)
        chunkSystemResourceId.read(data)
        while (true) {
            val index = data.position()
            if (index == chunkSize) break
            val type = data.short
            data.position(index)
            when (type) {
                ChunkStartTag.TYPE -> {
                    val startTag = ChunkStartTag()
                    startTag.read(data)
                    chunkTags.add(startTag)
                }

                ChunkEndTag.TYPE -> {
                    val endTag = ChunkEndTag()
                    endTag.read(data)
                    chunkTags.add(endTag)
                }

                ChunkNamespace.TYPE_START -> {
                    val namespace = ChunkNamespace()
                    namespace.read(data)
                    chunkStartNamespace[namespace.uri] = namespace
                }

                ChunkNamespace.TYPE_END -> {
                    val namespace = ChunkNamespace()
                    namespace.read(data)
                    chunkEndNamespace[namespace.uri] = namespace
                }
            }
        }
        val f = 0
    }

    fun getString(index: Int): String {
        return chunkString.getString(index)
    }

    private val sb = StringBuilder()

    // private var lineNumber = 1
    override fun toString(): String {
        sb.clear()
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
        var depth = 0
        breakLine(depth)
        var addNamespace = false
        chunkTags.forEach {
            if (it.type == ChunkStartTag.TYPE) {
                it as ChunkStartTag
                /*repeat(max(1, it.lineNumber - lineNumber)) {
                    breakLine(0)
                }*/
                breakLine(depth)
                sb.append("<")
                sb.append(getString(it.name))

                if (!addNamespace) {
                    addNamespace = true
                    chunkStartNamespace.forEach { namespace ->
                        breakLine(depth + 1)
                        sb.append("xmlns:")
                        sb.append(getString(namespace.value.prefix))
                        sb.append("=\"")
                        sb.append(getString(namespace.value.uri))
                        sb.append("\"")
                    }
                }

                for (attr in it.attributes) {
                    breakLine(depth + 1)
                    if (attr.namespaceUri >= 0) {
                        sb.append(getString(chunkStartNamespace[attr.namespaceUri]!!.prefix))
                        sb.append(":")
                    }
                    sb.append(getString(attr.name))
                    sb.append("=\"")
                    if (attr.value != -1) {
                        sb.append(getString(attr.value))
                    } else {
                        sb.append(attr.resValue.data)
                    }
                    sb.append("\"")
                }
                sb.append(">")
                depth++
            } else {
                depth--
                it as ChunkEndTag
                breakLine(depth)
                sb.append("</")
                sb.append(getString(it.name))
                sb.append(">")
            }
        }
        return sb.toString()
    }

    private fun breakLine(depth: Int) {
        sb.append("\n")
        indent(depth)
    }

    private fun indent(depth: Int) {
        repeat(depth) {
            sb.append("\t")
        }
    }

    private fun addKeyValue() {
    }
}

/**
 * 字符串区域
 * 如何将int对应到字符串
 * 其它地方的int字符代表着字符串下标([stringOffsets]的下标)
 * 从[stringOffsets]对应下标中取出offset，用offset去[stringPools]中取出具体的值
 */

class ChunkString : BaseChunk() {
    companion object {
        const val HEADER_SIZE = 0x8
    }

    var stringCount: Int = 0
    var styleCount: Int = 0
    var isUTF8: Short = 0
    var isSorted: Short = 0

    // 从该chunk开始计数的start
    var stringsStart: Int = 0
    var styleStart: Int = 0

    // stringCount * 4
    // 这是一段连续的Index数组，每个index占4字节(对应java的Integer)，各个数字表示对应的各个字符串，从StringStart开始的偏移量
    val stringOffsets = ArrayList<Int>()

    // styleAccount * 4
    val styleOffset = ArrayList<Int>()

    val stringPools = HashMap<Int, StringPool>()
    override fun onRead(data: ByteBuffer) {
        stringCount = data.int
        styleCount = data.int
        isUTF8 = data.short
        isSorted = data.short
        stringsStart = data.int
        styleStart = data.int
        repeat(stringCount) {
            stringOffsets.add(data.int)
        }
        repeat(styleCount) {
            styleOffset.add(data.int)
        }
        val startPosition = data.position()
        println("开始读取string ${data.position()}")
        repeat(stringCount) {
            data.position(startPosition + stringOffsets[it])
            val stringPool = StringPool(isUTF8.toInt() != 0)
            stringPool.read(data)
            stringPools[stringOffsets[it]] = stringPool
        }
    }

    fun getString(index: Int): String {
        val offset = stringOffsets[index]
        return stringPools[offset]!!.chars
    }

    /**
     * 单条字符串信息
     */
    class StringPool(private val isUTF8: Boolean) : IRead {
        var len: Short = 0

        // isUTF8 ? 1byte * byteLength : 2byte * charLength
        lateinit var chars: String

        // isUTF8 ? 1byte : 2byte
        var separator: Short = 0
        override fun read(data: ByteBuffer) {
            // charLength = data.get()
            // byteLength = data.get()
            if (isUTF8) {
                data.get()
                val t = data.get().toShort()
                len = (t and 0xFF.toShort())
                println("read 1--> ${data.position()}")
                val c = ByteArray(len.toInt()) {
                    data.get()
                }
                chars = String(c)
                println("read 2--> ${data.position()} $chars")
                separator = data.get().toShort()
            } else {
                val size = data.short
                println("read 1--> ${data.position()}")
                val c = ByteArray(size * 2) {
                    data.get()
                }
                chars = String(c)
                val f = chars
                println(chars)
                println("read 2--> ${data.position()}")
                separator = data.short
            }
        }

        companion object {
            fun create(string: String, isUTF8: Boolean): StringPool {
                val pool = StringPool(isUTF8)
                pool.chars = string
                return pool
            }
        }
    }
}

/**
 * 系统资源id表
 */
class ChunkSystemResourceId : BaseChunk() {

    // 长度为（chunkSize-8）4
    lateinit var resourceIds: IntArray
    override fun onRead(data: ByteBuffer) {
        resourceIds = IntArray((chunkSize - 8) / 4) {
            data.int
        }
    }
}

/**
 * 开始名称空间区块
 */
class ChunkNamespace : BaseChunk() {
    companion object {
        const val TYPE_START = 0x100.toShort()
        const val TYPE_END = 0x101.toShort()
    }

    var lineNumber: Int = 0
    var comment: Int = 0
    var prefix: Int = 0
    var uri: Int = 0
    override fun onRead(data: ByteBuffer) {
        lineNumber = data.int
        comment = data.int
        prefix = data.int
        uri = data.int
    }
}

class ChunkStartTag : BaseTagChunk() {
    companion object {
        const val TYPE = 0x0102.toShort()
    }

    var attrStart: Short = 0

    // chunkAttr 大小
    var attrSize: Short = 0
    var attrCount: Short = 0
    var idIndex: Short = 0

    // 当前tag的class属性，如果0，则表示没有，1，则表示第一个属性是class
    var classIndex: Short = 0
    var styleIndex: Short = 0
    lateinit var attributes: ArrayList<Attribute>
    override fun onRead2(data: ByteBuffer) {
        attrStart = data.short
        attrSize = data.short
        attrCount = data.short
        idIndex = data.short
        classIndex = data.short
        styleIndex = data.short
        attributes = ArrayList()
        repeat(attrCount.toInt()) {
            val attr = Attribute()
            attr.read(data)
            attributes.add(attr)
        }
    }
}

class Attribute : IRead {
    var namespaceUri: Int = 0
    var name: Int = 0

    // 如果value==-1，则需要取resValue中获取
    var value: Int = 0
    lateinit var resValue: ResValue
    override fun read(data: ByteBuffer) {
        namespaceUri = data.int
        name = data.int
        value = data.int
        resValue = ResValue()
        resValue.read(data)
    }

    class ResValue : IRead {
        var size: Short = 0
        var res0: Byte = 0
        var type: Byte = 0
        var data: Int = 0
        override fun read(data: ByteBuffer) {
            size = data.short
            res0 = data.get()
            type = data.get()
            this.data = data.int
        }
    }
}

class ChunkEndTag : BaseTagChunk() {
    companion object {
        const val TYPE = 0x0103.toShort()
    }

    override fun onRead2(data: ByteBuffer) {
    }
}
