package com.example.viewdebug.xml.struct.writer

import com.example.viewdebug.xml.struct.XmlCompiler
import org.w3c.dom.Node

interface AttributeConvertor {
    fun onParsed(tag: Node, compiler: XmlCompiler)

}

/**
 * 将某个属性转换为其它属性，并使用转换前的值，而且保留原始属性
 * @param ns 需要转换的命名空间
 * @param attribute 需要转换的属性名称
 * @param toNs 需要转换成什么命名空间
 * @param toAttribute 需要转换成什么属性
 * @param overwrite 是否覆盖已有属性
 */
open class DefaultAttributeConvertor(private val ns: String, private val attribute: String, private val toNs: Array<String>, private val toAttribute: Array<String>, private val overwrite: Boolean) : AttributeConvertor {
    override fun onParsed(tag: Node, compiler: XmlCompiler) {
        val attributeNode = tag.attributes.getNamedItem("$ns:$attribute")
        if (attributeNode != null) {
            toNs.forEachIndexed { index, ns ->
                compiler.addAttribute(tag.nodeName, ns, toAttribute[index], attributeNode.nodeValue, overwrite)
            }
        }
    }
}

/**
 * [DefaultAttributeConvertor]的简化使用类，省略重复参数，只能转换”android“命名空间下的属性
 */
class AndroidAttributeConvertor(attributeName:String, toName: Array<String>, overwrite: Boolean): DefaultAttributeConvertor("android", attributeName, toNs = Array(toName.size) { "android"}, toAttribute = toName, overwrite = overwrite)