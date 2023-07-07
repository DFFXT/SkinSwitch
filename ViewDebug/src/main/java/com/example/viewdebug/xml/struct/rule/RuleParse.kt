package com.example.viewdebug.xml.struct.rule

import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

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
    val attrMap = HashMap<String, ArrayList<AttrDesc>>()

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

        val f = 0
    }

    private fun addNode(parent: String, node: Node) {
        if (node.nodeType == Node.ELEMENT_NODE) {
            // 解析属性
            if (node.nodeName == "attr") {
                parseAttr(parent, node)
            } else if (node.nodeName == "declare-styleable") {
                // 解析ViewGroup
                val name = node.attributes.getNamedItem("name").nodeValue
                for (i in 0 until node.childNodes.length) {
                    addNode(name, node.childNodes.item(i))
                }
            }
        }
    }

    private fun parseAttr(parent: String, node: Node) {
        val attr = node.attributes.getNamedItem("name")
        val format = node.attributes.getNamedItem("format")
        val desc = AttrDesc(parent, format?.nodeValue)
        var descList = attrMap[attr.nodeValue]
        if (descList == null) {
            descList = ArrayList()
            attrMap[attr.nodeValue] = descList
        }
        descList.add(desc)
        if (node.hasChildNodes()) {
            for (i in 0 until node.childNodes.length) {
                val childNode = node.childNodes.item(i)
                if (childNode.nodeType == Node.ELEMENT_NODE && childNode.nodeName == "enum") {
                    if (childNode.hasAttributes()) {
                        val enumName = childNode.attributes.getNamedItem("name").nodeValue
                        val enumValue = childNode.attributes.getNamedItem("value").nodeValue
                        desc.addEnum(enumName, enumValue)
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
            for (desc in it) {
                if (desc.getEnumValue(rowValue) != null) {
                    return ValueData(
                        type = desc.format,
                        value = desc.getEnumValue(rowValue),
                    )
                }
            }
            // 找类型
            for (desc in it) {
                if (desc.format != null) {
                    return ValueData(
                        type = desc.format,
                        value = desc.getEnumValue(rowValue),
                    )
                }
            }
        }
        return null
    }

    class AttrDesc(
        // 当前属性所属，""、ViewGroup_Layout、TextView_Layout、ConstraintLayout_Layout、XXView等
        val parent: String,
        val format: String?,
        private val enumMap: HashMap<String, String> = HashMap<String, String>(),
    ) {
        fun addEnum(key: String, value: String) {
            enumMap[key] = value
        }

        fun getEnumValue(key: String): String? {
            return enumMap[key]
        }
    }

    data class ValueData(
        val type: String?,
        val value: String?,
    )
}
