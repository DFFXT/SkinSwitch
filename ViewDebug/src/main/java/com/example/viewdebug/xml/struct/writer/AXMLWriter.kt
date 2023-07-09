package com.example.viewdebug.xml.struct.writer

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.reader.IWrite
import com.skin.log.Logger
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

/**
 * chunk信息写入
 * @param startPosition 该chunk信息在buffer中的开始位置
 */
abstract class BaseChunkWriter(var startPosition: Int) : IWrite {
    var type: Short = 0
    var headerSize: Short = 0
    var chunkSize: Int = 0
    override fun write(data: ByteBuffer) {
        log("write on ${data.position()}  ${this.javaClass.simpleName}")
        data.position(startPosition)
        data.putShort(type)
        data.putShort(headerSize)
        data.putInt(chunkSize)
        onWrite(data)
        writeChunkSize(data, data.position() - startPosition)
    }

    /**
     * 写入chunkSize
     */
    fun writeChunkSize(data: ByteBuffer, size: Int) {
        chunkSize = size
        data.putInt(startPosition + 4, size)
    }

    protected abstract fun onWrite(data: ByteBuffer)
}

/**
 * base tag 信息写入
 */
abstract class BaseTagChunkWriter(startPosition: Int) : BaseChunkWriter(startPosition) {
    var lineNumber: Int = 0
    var comment: Int = -1
    var namespaceUri: Int = -1
    var name: Int = -1
    override fun onWrite(data: ByteBuffer) {
        data.putInt(lineNumber)
        data.putInt(comment)
        data.putInt(namespaceUri)
        data.putInt(name)
        onWrite2(data)
        writeChunkSize(data, data.position() - startPosition)
    }

    abstract fun onWrite2(data: ByteBuffer)
}

/**
 * xml 文件写入
 */
class ChunkFileWriter : BaseChunkWriter(0) {
    companion object {
        const val HEADER_SIZE = 0x8.toShort()
    }

    init {
        type = 0x03
        headerSize = HEADER_SIZE
    }

    // 字符串常量池
    val chunkString: ChunkStringWriter = ChunkStringWriter(0)
    val chunkSystemResourceId: ChunkSystemResourceIdWriter = ChunkSystemResourceIdWriter(0)
    var chunkStartNamespace = HashMap<Int, ChunkNamespaceWriter>()
    var chunkEndNamespace = HashMap<Int, ChunkNamespaceWriter>()
    val chunkTags = LinkedList<BaseTagChunkWriter>()
    override fun onWrite(data: ByteBuffer) {
        chunkString.startPosition = data.position()
        chunkString.write(data)
        // data.position(chunkString.chunkSize + headerSize)
        chunkSystemResourceId.startPosition = data.position()
        chunkSystemResourceId.write(data)
        chunkStartNamespace.forEach {
            it.value.startPosition = data.position()
            it.value.write(data)
        }
        chunkTags.forEach {
            it.startPosition = data.position()
            it.write(data)
        }
        chunkEndNamespace.forEach {
            it.value.startPosition = data.position()
            it.value.write(data)
        }
    }
}

/**
 * 字符串区域信息写入
 * 如何将int对应到字符串
 * 其它地方的int字符代表着字符串下标([stringOffsets]的下标)
 * 从[stringOffsets]对应下标中取出offset，用offset去[stringPools]中取出具体的值
 */
class ChunkStringWriter(startPosition: Int) : BaseChunkWriter(startPosition) {
    companion object {
        const val HEADER_SIZE = 0x1c.toShort()
        const val TYPE: Short = 0x1
    }

    init {
        headerSize = HEADER_SIZE
        type = TYPE
    }

    var stringCount: Int = 0
    var styleCount: Int = 0
    var isUTF8: Short = 256
    var isSorted: Short = 0

    // 从该chunk开始计数的start
    var stringsStart: Int = 28
    var styleStart: Int = 0

    // stringCount * 4
    // 这是一段连续的Index数组，每个index占4字节(对应java的Integer)，各个数字表示对应的各个字符串，从StringStart开始的偏移量
    val stringOffsets = ArrayList<Int>()

    // styleAccount * 4
    val styleOffset = ArrayList<Int>()

    val stringPools = LinkedHashMap<Int, StringPool>()

    /**
     * todo 有可能发生错误，[StringPool.cpp]中是先读取的styleOffset再读取的stringOffset
     * 那么这里其实应该先写stylePool再写stringPool
     * 因为stylePool一直为0，所以不会读取错误
     */
    override fun onWrite(data: ByteBuffer) {
        data.putInt(stringOffsets.size)
        data.putInt(styleOffset.size)

        data.putShort(isUTF8)
        data.putShort(isSorted)
        data.putInt(stringsStart + stringOffsets.size * 4 + styleOffset.size * 4)
        data.putInt(0)
        stringOffsets.forEach {
            data.putInt(it)
        }
        styleOffset.forEach {
            data.putInt(it)
        }
        println("开始保存string ${data.position()}")
        var i = 0
        stringPools.forEach {
            log("write string start start ${data.position()}")
            it.value.isUTF8 = isUTF8 != 0.toShort()
            it.value.write(data)
            log("write string ${it.value.string}")
            log("write string end start ${data.position()}")
            i++
        }
        // todo stylePool


        // todo 这里需要特别注意，需要进行4字节对齐,
        // [frameworks/base/tools/aapt2/StringPool.cpp]中在读取完字符串后，进行了，out->Align4()操作
        // 经查看源码，会进行4字节取余，然后在out中进行偏移
        // 那么，在写入时得考虑这个问题，长度不足4的整数倍需要补齐
        val padding = data.position() % 4
        repeat(4 -padding) {
            data.put(0)
        }
    }

    fun getString(index: Int): String {
        val offset = stringOffsets[index]
        return stringPools[offset]!!.chars
    }

    /**
     * 单条字符串信息写入
     */
    class StringPool(val string: String) : IWrite {

        companion object {
            // 额外内容
            const val extra_size_utf8 = 3

            // 额外内容
            const val extra_size_not_utf8 = 4
        }

        var isUTF8: Boolean = false
        var len: Short = 0

        // isUTF8 ? 1byte * byteLength : 2byte * charLength
        lateinit var chars: String

        // isUTF8 ? 1byte : 2byte
        var separator: Short = 0
        override fun write(data: ByteBuffer) {
            // charLength = data.get()
            // byteLength = data.get()
            val strByte = string.toByteArray()
            if (isUTF8) {
                data.put(0)
                // data.get()
                data.put(strByte.size.toByte())
                println("-->write str start:${data.position()}")
                data.put(strByte)
                println("-->write str end:${data.position()}")

                // val t = data.get().toShort()
                /*len = (t and 0xFF.toShort())
                val c = ByteArray(len.toInt()) {
                    data.get()
                }
                chars = String(c)
                val f = chars*/
                data.put(0)
            } else {
                data.putShort((strByte.size / 2).toShort())
                println("-->write str start $string:${data.position()}")
                data.put(strByte)
                println("-->write str end:${data.position()}")
                /*val c = ByteArray(data.short * 2) {
                    data.get()
                }
                chars = String(c)
                val f = chars*/
                data.putShort(0)
            }

            // separator = data.short
        }
    }
}

/**
 * 系统资源id表信息写入
 */
class ChunkSystemResourceIdWriter(startPosition: Int) : BaseChunkWriter(startPosition) {
    companion object {
        const val TYPE: Short = 0x0180
    }

    init {
        type = TYPE
        headerSize = 0x8
    }

    // 长度为（chunkSize-8）4
    val resourceIds = LinkedHashSet<Int>()
    override fun onWrite(data: ByteBuffer) {
        // 这里需要排序
        resourceIds.sorted().forEach {
            log("???? $it")
            data.putInt(it)
        }
    }
}

/**
 * 开始命名空间区块写入
 */
class ChunkNamespaceWriter(startPosition: Int, type: Short) : BaseChunkWriter(startPosition) {
    companion object {
        const val TYPE_START = 0x100.toShort()
        const val TYPE_END = 0x101.toShort()
        fun create(prefix: String, uri: String): ChunkNamespaceWriter {
            return ChunkNamespaceWriter(0, 0)
        }
    }

    init {
        this.type = type.toShort()
        this.headerSize = 0x10
    }

    var lineNumber: Int = 0
    var comment: Int = 0
    var prefix: Int = 0
    var uri: Int = 0
    override fun onWrite(data: ByteBuffer) {
        data.putInt(lineNumber)
        data.putInt(comment)
        data.putInt(prefix)
        data.putInt(uri)
    }
}

/**
 * 开始tag写入
 */
class ChunkStartTagWriter(startPosition: Int) : BaseTagChunkWriter(startPosition) {
    companion object {
        const val TYPE = 0x0102.toShort()
    }

    init {
        type = TYPE
        headerSize = 0x10
    }

    var attrStart: Short = 0x14.toShort()

    // chunkAttr 大小
    var attrSize: Short = 0x14.toShort()
    var attrCount: Short = 0
    var idIndex: Short = 0
    var classIndex: Short = 0
    var styleIndex: Short = 0
    val attributes = ArrayList<Attribute>()
    override fun onWrite2(data: ByteBuffer) {
        data.putShort(attrStart)
        data.putShort(attrSize)
        data.putShort(attributes.size.toShort())
        data.putShort(idIndex)
        data.putShort(classIndex)
        data.putShort(styleIndex)
        // todo 属性标签需要通过 R.attr.xxx 的值从小到大排序
        attributes.sortBy { it.systemResourceId }
        attributes.forEach {
            it.write(data)
        }
    }
}

/**
 * 属性写入
 * @param systemResourceId 属性对应id，不参与write，只参与排序
 */
class Attribute(var systemResourceId: Int) : IWrite {
    var namespaceUri: Int = -1
    var name: Int = 0

    // 如果value==-1，则需要取resValue中获取
    var value: Int = -1
    lateinit var resValue: ResValue
    override fun write(data: ByteBuffer) {
        data.putInt(namespaceUri)
        data.putInt(name)
        data.putInt(value)
        resValue.write(data)
    }

    class ResValue : IWrite {
        var size: Short = 8

        // 恒定0
        var res0: Byte = 0

        // value类型，比如整数（分10进制、8进制等）、dp、sp、引用，具体只见[struct Res_value]
        // 这里默认是int10进制
        // todo
        var type: Byte = 0x10
        var data: Int = 0

        var parentValue = false
        override fun write(data: ByteBuffer) {
            data.putShort(size)
            data.put(res0)
            data.put(type)
            data.putInt(this.data)
        }
    }
}

/**
 * 结束tag写入
 */
class ChunkEndTagWriter(startPosition: Int) : BaseTagChunkWriter(startPosition) {
    companion object {
        const val TYPE = 0x0103.toShort()
    }

    init {
        this.type = TYPE
        this.headerSize = 0x10
    }

    override fun onWrite2(data: ByteBuffer) {
    }
}

private fun log(string: String) {
    Logger.i("XmlWrite", string)
}