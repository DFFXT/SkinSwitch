package com.example.viewdebug.xml.struct.rule

import com.skin.log.Logger
import org.w3c.dom.Node
import java.io.InputStream
import java.util.LinkedList
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.max

/**
 * 将value.xml等文件解析成规则
 * @param nsPrefix 命名空间，比如：android、app等
 */
class RuleParse(val nsPrefix: String) {
    /**
     * 解析结果
     * key：属性名称
     * value：属性值，由于文件中会存在多个declare-styleable下有相同属性的情况，所以用list存储
     */
    val attrMap = HashMap<String, AttrDesc>()

    fun parse(stream: InputStream) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(stream)
        for (i in 0 until doc.childNodes.length) {
            val node = doc.childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                for (j in 0 until node.childNodes.length) {
                    addNode("", node.childNodes.item(j))
                }
            }
        }

    }

    private fun addNode(parent: String, node: Node) {
        if (node.nodeType == Node.ELEMENT_NODE) {
            // 解析属性
            if (node.nodeName == "attr") {
                parseAttr(parent, node)
            } else {
                // 解析ViewGroup
                val name = node.attributes.getNamedItem("name")?.nodeValue ?: ""
                for (i in 0 until node.childNodes.length) {
                    addNode(name, node.childNodes.item(i))
                }
            }
        }
    }

    private fun parseAttr(parent: String, node: Node) {
        val attr = node.attributes.getNamedItem("name").nodeValue
        // 如果当前属性是以android:开头，那么属于引用，直接掠过
        if (attr.startsWith("android:")) return
        val format = node.attributes.getNamedItem("format")?.nodeValue
        var desc = attrMap[attr]
        if (desc == null) {
            desc = AttrDesc(parent)
            attrMap[attr] = desc
        }
        desc.addFormat(format)

        if (node.hasChildNodes()) {
            for (i in 0 until node.childNodes.length) {
                val childNode = node.childNodes.item(i)
                if (childNode.nodeType == Node.ELEMENT_NODE && childNode.nodeName == "enum") {
                    // 枚举类型
                    if (childNode.hasAttributes()) {
                        val enumName = childNode.attributes.getNamedItem("name").nodeValue
                        val enumValue = childNode.attributes.getNamedItem("value").nodeValue
                        desc.addEnum(enumName, enumValue)
                    }
                } else if (childNode.nodeType == Node.ELEMENT_NODE && childNode.nodeName == "flag") {
                    // flag类型，比如：end|top
                    if (childNode.hasAttributes()) {
                        val flagName = childNode.attributes.getNamedItem("name").nodeValue
                        val flagValue = childNode.attributes.getNamedItem("value").nodeValue
                        // 目前暂时只考虑flag为16进制和10进制的情况
                        val value = if (flagValue.startsWith("0x")) {
                            flagValue.substring(2, flagValue.length).toUInt(16)
                        } else {
                            // 对于不是16进制的情况，则需要转换成int类型，负数无法转uint
                            flagValue.toInt(10).toUInt()
                        }
                        desc.addFlag(flagName, value)
                    }
                }
            }
        }
    }

    /**
     * 根据属性、属性值，找到数据类型，数据对应的值
     * 如果[ValueData.value] == null 则说明应该判断type来确定是否是引用还是其他类型
     */
    fun getValue(tagName: String, attrName: String, rowValue: String): ValueData? {
        attrMap[attrName]?.let {
            // 找枚举
            if (it.getEnumValue(rowValue) != null) {
                return ValueData(
                    type = it.format,
                    value = it.getEnumValue(rowValue),
                )
            }
            if (it.getFlag(rowValue) != null) {

            }
            // 找flag
            return ValueData(
                type = it.format,
                value = it.getFlag(rowValue)?.toString(),
            )
        }
        return null
    }

    class AttrDesc(
        // 当前属性所属，""、ViewGroup_Layout、TextView_Layout、ConstraintLayout_Layout、XXView等
        @Deprecated("")
        val parent: String
        ) {
        val format: HashSet<String> = HashSet()
        private val enumMap: HashMap<String, String> = HashMap<String, String>()
        private val flagMap: HashMap<String, UInt> = HashMap()

        fun addFormat(format: String?) {
            format ?: return
            if (format.contains("|")) {
                val f = format.split("|")
                this.format.addAll(f)
            } else {
                this.format.add(format)
            }
        }
        fun addEnum(key: String, value: String) {
            if (enumMap.contains(key)) {
                if (enumMap[key] != value) {
                    Logger.e("RuleParse", "warning addEnum key= $key, $value replaced ${enumMap[key]}")
                }
            }
            enumMap[key] = value
        }

        fun getEnumValue(key: String): String? {
            return enumMap[key]
        }

        fun addFlag(key: String, value: UInt) {
            if (flagMap.contains(key)) {
                if (flagMap[key] != value) {
                    Logger.e("RuleParse", "warning addFlag key= $key, $value replaced ${flagMap[key]}")
                }
            }
            flagMap[key] = value
        }

        /**
         * 当前描述是否有效
         */
        fun isValid(): Boolean {
            return format != null || enumMap.size > 0 || flagMap.size > 0
        }

        /**
         * 获取flag，
         */
        fun getFlag(key: String): UInt? {
            val keys = key.trim()

            if (keys.contains("|")) {
                val keyList = keys.split("|")
                var result:UInt = 0u
                for (k in keyList) {
                    val v = flagMap[k]
                    if (v != null) {
                        result = result or v
                    }
                }
                return result
            } else {
                return flagMap[keys]
            }
        }

        /**
         * 优先级，由于规则文件中有很多重复的定义，
         * 所以需要定义优先级，首先是枚举优先级最高，然后是有flag的次之，没有任何约束的优先级最低
         */
        fun getPriority(): Int {
            return max(enumMap.size, flagMap.size)
        }
    }

    /**
     * 返回结果
     * @param type <attr format="xxx"> 中的xxx
     * @param value 如果当前属性需要进行转换，比如：枚举、flag等，则会将结果返回，否则为null
     */
    data class ValueData(
        val type: HashSet<String>,
        val value: String?,
    )
}
