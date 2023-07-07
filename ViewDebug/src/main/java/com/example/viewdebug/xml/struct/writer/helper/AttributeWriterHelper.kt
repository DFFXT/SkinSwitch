package com.example.viewdebug.xml.struct.writer.helper

import com.example.viewdebug.xml.AndroidXmlManager
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.helper.value.AttReferenceValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrValueCompile
import com.skin.log.Logger

class AttributeWriterHelper(private val compiler: XmlCompiler) {
    private val attrValueCompiles = HashMap<String, AttrValueCompile>()

    init {
        addCompiler(AttrColorValueCompile())
        addCompiler(AttReferenceValueCompile())
    }

    fun addCompiler(compile: AttrValueCompile) {
        attrValueCompiles[compile.type] = compile
    }

    /**
     * 编译属性值
     */
    fun compileAttributeResValue(tagName: String, attrName: String, attrValue: String, nsPrefix: String?): Attribute.ResValue? {
        val result = AndroidXmlManager.getValue(tagName, attrName, attrValue, nsPrefix!!)
        if (result != null) {
            Logger.i("AttributeWriterHelper", "type ${result.type}")
            var singleType = result.type
            if (result.type!!.contains("|")) {
                singleType = result.type.split("|")[0]
            }
            // 说明不是枚举类型
            if (result.value == null) {
                if (attrValue.startsWith("@")) {
                    // 是引用类型
                    val pair = attrValueCompiles["reference"]!!.compile(attrValue)!!
                    return Attribute.ResValue().apply {
                        this.data = pair.second
                        this.type = pair.first
                    }
                } else {
                    // 说明不是引用
                    val pair = attrValueCompiles[singleType]!!.compile(attrValue)!!
                    return Attribute.ResValue().apply {
                        this.data = pair.second
                        this.type = pair.first
                    }
                }
            } else {
                // 说明是枚举类型
                return Attribute.ResValue().apply {
                    this.data = compiler.addString(attrValue)
                    this.type = ResourceType.TYPE_STRING
                }
            }



            // todo
        }
        return null
    }
}
