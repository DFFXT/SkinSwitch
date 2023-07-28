package com.example.viewdebug.xml.struct.writer

import com.example.viewdebug.xml.struct.reader.IWrite
import com.skin.log.Logger
import java.nio.ByteBuffer
import java.util.*

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
abstract class BaseTagChunkWriter(startPosition: Int, private val chunkFileWriter: ChunkFileWriter) : BaseChunkWriter(startPosition) {
    var lineNumber: Int = 0
    var comment: Int = -1
    var namespaceUri: String = ""
    lateinit var name: String
    override fun onWrite(data: ByteBuffer) {
        data.putInt(lineNumber)
        data.putInt(comment)
        data.putInt(chunkFileWriter.chunkString.getStringIndex(namespaceUri))
        data.putInt(chunkFileWriter.chunkString.getStringIndex(name))
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
    var chunkStartNamespace = ArrayList<ChunkNamespaceWriter>()
    var chunkEndNamespace = ArrayList<ChunkNamespaceWriter>()
    val chunkTags = LinkedList<BaseTagChunkWriter>()
    override fun onWrite(data: ByteBuffer) {
        chunkString.startPosition = data.position()
        chunkString.write(data)
        // data.position(chunkString.chunkSize + headerSize)
        chunkSystemResourceId.startPosition = data.position()
        chunkSystemResourceId.write(data)
        chunkStartNamespace.forEach {
            it.startPosition = data.position()
            it.write(data)
        }
        chunkTags.forEach {
            it.startPosition = data.position()
            it.write(data)
        }
        chunkEndNamespace.forEach {
            it.startPosition = data.position()
            it.write(data)
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

        const val PRIORITY_TYPE_ATTR_NAME = 4
        const val PRIORITY_TYPE_NS = 3
        const val PRIORITY_TYPE_ATTR_VALUE = 2
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

    private val stringPools = HashMap<String, StringPool>()
    private lateinit var sortedStringPool: ArrayList<StringPool>

    // key type
    private val attrNames = ArrayList<StringPool>()

    private val otherStrings = ArrayList<StringPool>()


    /**
     * 新增字符串
     * 根据解析争取的axml文件
     * 发现属性名称是第一优先级
     *  属性名称内部根据对应的属性id小到大排序
     * 其它是第二优先级，
     *  其它字符串内部根据ASCII排序，中午还不清楚，也应该是根据其每个的byte数值来排序
     */
    fun addString(type: Int, priority: Int, string: String) {
        if (!stringPools.contains(string)) {
            val pool = StringPool(priority.toLong() or (type.toLong() shl 32), string)
            stringPools[string] = pool
            if (type == PRIORITY_TYPE_ATTR_NAME) {
                attrNames.add(pool)
            } else {
                otherStrings.add(pool)
            }
        }
    }

    /**
     * 对所有文本进行排序
     */
    private fun sort() {
        var preOffset = 0
        val sortedAttrNames = attrNames.sortedBy { it.priority }
        val sortedOthers = otherStrings.sortedBy { it.string }
        sortedStringPool = ArrayList()
        sortedStringPool.addAll(sortedAttrNames)
        sortedStringPool.addAll(sortedOthers)
        sortedStringPool.forEachIndexed { index, stringPool ->
            stringPool.index = index
            stringPool.isUTF8 = isUTF8 != 0.toShort()
            stringOffsets.add(index, preOffset)
            preOffset += stringPool.getChunkSize()
        }
    }

    /**
     * 获取字符串在池子中的位置
     * 必须在sort之后才有效
     */
    fun getStringIndex(string: String): Int {
        return stringPools[string]?.index ?: -1
    }

    /**
     * todo 有可能发生错误，[StringPool.cpp]中是先读取的styleOffset再读取的stringOffset
     * 那么这里其实应该先写stylePool再写stringPool
     * 因为stylePool一直为0，所以不会读取错误
     */
    override fun onWrite(data: ByteBuffer) {
        sort()
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
        sortedStringPool.forEachIndexed { index, it ->
            log("write string start start ${data.position()}")
            it.write(data)
            log("write string ${it.string}")
            log("write string end start ${data.position()}")
            i++
        }
        // todo stylePool


        // todo 这里需要特别注意，需要进行4字节对齐,
        // [frameworks/base/tools/aapt2/StringPool.cpp]中在读取完字符串后，进行了，out->Align4()操作
        // 经查看源码，会进行4字节取余，然后在out中进行偏移
        // 那么，在写入时得考虑这个问题，长度不足4的整数倍需要补齐

        val padding = data.position() % 4
        //根据对比Android studio实际编译结果 刚好的情况下还需要补4字节
        if (padding == 0) {
            data.putInt(0)
        } else {
            repeat(4 - padding) {
                data.put(0)
            }
        }

    }

    /**
     * 单条字符串信息写入
     */
    class StringPool(val priority: Long, val string: String) : IWrite {

        companion object {
            // 额外内容
            const val extra_size_utf8 = 3

            // 额外内容
            const val extra_size_not_utf8 = 4
        }

        var index = 0

        var isUTF8: Boolean = false
        var len: Short = 0

        // isUTF8 ? 1byte * byteLength : 2byte * charLength
        // lateinit var chars: String

        // isUTF8 ? 1byte : 2byte
        var separator: Short = 0
        override fun write(data: ByteBuffer) {
            // charLength = data.get()
            // byteLength = data.get()
            /**
             * 经分析：
             * 长度<128
             * length:1字节
             * byteSize:1字节
             * 长度>=128:
             * length:2字节，其中前一个字节为80(根据进位得来)
             * 长度>=256:
             * length:2字节，其中前一个字节81(根据进位得来)
             *
             * 举例：
             * 129 --> 8081(1000,0000,1000,0001)
             * 260 --> 8104(1000,0001,0000,0100)
             * 推测逻辑：
             *     如果第一个字节<128，则说明字符串长度小于128
             *     如果第一个字节>=128，则说明字符串长度大于等于128
             *         如果是第二种情况，则读取两个字节，short or 011111111111111，将高位置为0
             *
             */
            val strByte = string.toByteArray()
            if (isUTF8) {
                val len = strByte.size

                // 写入char size
                writeLen(data, string.length)
                //data.put(string.length.toByte())
                // 写入byte size
                writeLen(data, len)
                //data.put(len)
                println("-->write str start:${data.position()}")
                data.put(strByte)
                println("-->write str end:${data.position()}")
                data.put(0)
            } else {
                data.putShort((strByte.size / 2).toShort())
                println("-->write str start $string:${data.position()}")
                data.put(strByte)
                println("-->write str end:${data.position()}")
                data.putShort(0)
            }
        }
        private fun writeLen(data: ByteBuffer, len: Int) {
            if (len > 0x7F) {
                val value = (0x8000 or len)
                // 这里写入长度是高位在左侧
                data.put(((value shr 8) and 0xff).toByte())
                data.put((value and 0xff).toByte())
            } else {
                data.put(len.toByte())
            }
        }

        fun getChunkSize(): Int {
            val byteSize = string.toByteArray().size
            var extra = 0
            if (byteSize > 0x7f) {
                extra += 1
            }
            if (string.length > 0x7f) {
                extra += 1
            }
            return if (isUTF8) {
                byteSize + extra_size_utf8 + extra
            } else {
                byteSize + extra_size_not_utf8 + extra
            }
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
            data.putInt(it)
        }
    }
}

/**
 * 开始命名空间区块写入
 */
class ChunkNamespaceWriter(startPosition: Int, type: Short, private val chunkFileWriter: ChunkFileWriter) : BaseChunkWriter(startPosition) {
    companion object {
        const val TYPE_START = 0x100.toShort()
        const val TYPE_END = 0x101.toShort()
    }

    init {
        this.type = type
        this.headerSize = 0x10
    }

    var lineNumber: Int = 0
    var comment: Int = 0
    lateinit var prefix: String
    lateinit var uri: String
    override fun onWrite(data: ByteBuffer) {
        data.putInt(lineNumber)
        data.putInt(comment)
        data.putInt(chunkFileWriter.chunkString.getStringIndex(prefix))
        data.putInt(chunkFileWriter.chunkString.getStringIndex(uri))
    }
}

/**
 * 开始tag写入
 */
class ChunkStartTagWriter(startPosition: Int, chunkFileWriter: ChunkFileWriter) : BaseTagChunkWriter(startPosition, chunkFileWriter) {
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
    private var idIndex: Short = 0
    private var classIndex: Short = 0
    private var styleIndex: Short = 0
    // 经过解析正确axml，属性名称也是需要排序的，按照属性对应的id进行排序
    val attributes = ArrayList<Attribute>()
    override fun onWrite2(data: ByteBuffer) {
        attributes.sortBy { it.systemResourceId }
        data.putShort(attrStart)
        data.putShort(attrSize)
        data.putShort(attributes.size.toShort())
        idIndex = (attributes.indexOfFirst { it.name == "id" && it.namespacePrefix == "android" } + 1).toShort()
        data.putShort(idIndex)
        classIndex = (attributes.indexOfFirst { it.name == "name" && it.namespacePrefix == "" } + 1).toShort()
        data.putShort(classIndex)
        styleIndex = (attributes.indexOfFirst { it.name == "style" && it.namespacePrefix == "" } + 1).toShort()
        data.putShort(styleIndex)


        attributes.forEach {
            it.write(data)
        }
    }
}

/**
 * 属性写入
 * @param systemResourceId 属性对应id，不参与write，只参与排序
 */
class Attribute(var systemResourceId: Int, private val chunkFileWriter: ChunkFileWriter) : IWrite {
    // 前缀
    var namespacePrefix: String = ""
    lateinit var name: String

    // 如果value==-1，则需要取resValue中获取
    private var value: Int = -1
    lateinit var resValue: ResValue
    override fun write(data: ByteBuffer) {
        // todo 理论上应该进行范围判定
        val nsUri = chunkFileWriter.chunkStartNamespace.find { it.prefix == namespacePrefix }?.uri
        // 写入命名空间
        data.putInt(chunkFileWriter.chunkString.getStringIndex(nsUri ?: ""))
        data.putInt(chunkFileWriter.chunkString.getStringIndex(name))
        if (resValue.stringData == null) {
            data.putInt(value)
        } else {
            resValue.data = chunkFileWriter.chunkString.getStringIndex(resValue.stringData!!)
            data.putInt(resValue.data)
        }
        resValue.write(data)
    }

    class ResValue : IWrite {
        var size: Short = 8

        // 恒定0
        var res0: Byte = 0

        // value类型，比如整数（分10进制、8进制等）、dp、sp、引用，具体只见[struct Res_value]
        // 这里默认是int10进制
        var type: Byte = 0x10
        var data: Int = 0

        // 如果不为null则，需要将其index赋值attr.data
        var stringData: String? = null
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
class ChunkEndTagWriter(startPosition: Int, chunkFileWriter: ChunkFileWriter) : BaseTagChunkWriter(startPosition, chunkFileWriter) {
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