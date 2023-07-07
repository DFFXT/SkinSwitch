package com.example.viewdebug.xml.struct.writer.helper

import com.example.viewdebug.xml.AndroidXmlManager
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.helper.value.AttReferenceValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrEnumValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrValueCompile
import com.skin.log.Logger

class AttributeWriterHelper(private val compiler: XmlCompiler) {
    private val attrValueCompiles = HashMap<String, AttrValueCompile>()

    init {
        addCompiler(AttrColorValueCompile())
        addCompiler(AttReferenceValueCompile())
        addCompiler(AttrEnumValueCompile())
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
            var singleType = result.type!!
            var realAttrValue = attrValue
            if (result.type.contains("|")) {
                singleType = result.type.split("|")[0]
            }
            // 说明不是枚举类型
            val compileType: String = if (result.value == null) {
                if (attrValue.startsWith("@")) {
                    // 是引用类型
                    "reference"
                } else {
                    // 说明不是引用
                    singleType
                }
            } else {
                realAttrValue = result.value
                "enum"
            }

            val pair = attrValueCompiles[compileType]!!.compile(realAttrValue, compiler)!!
            return Attribute.ResValue().apply {
                this.data = pair.second
                this.type = pair.first
            }



            // todo
        }
        return null
    }
}
