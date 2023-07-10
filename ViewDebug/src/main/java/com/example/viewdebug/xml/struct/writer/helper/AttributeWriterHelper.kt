package com.example.viewdebug.xml.struct.writer.helper

import com.example.viewdebug.xml.AndroidXmlManager
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.helper.value.AttReferenceValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrBooleanValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrDimensionValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrTransValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrStringValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrValueCompile
import com.skin.log.Logger
import java.lang.Exception

class AttributeWriterHelper(private val compiler: XmlCompiler) {
    private val attrValueCompiles = HashMap<String, AttrValueCompile>()

    init {
        addCompiler(AttrColorValueCompile())
        addCompiler(AttReferenceValueCompile())
        addCompiler(AttrTransValueCompile())
        addCompiler(AttrDimensionValueCompile())
        addCompiler(AttrBooleanValueCompile())
        addCompiler(AttrStringValueCompile())
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
            Logger.i("AttributeWriterHelper", "$tagName $attrName $attrValue type ${result.type}")
            var singleType = result.type
            var realAttrValue = attrValue



            val compileType = if (attrValue.startsWith("@")) {
                // 是引用类型
                "reference"
            } else {
                if (singleType?.contains("|") == true) {
                    val types = singleType.split("|")
                    // 找出非引用格式
                    for (type in types) {
                        if (singleType != AttrValueFormat.REFERENCE) {
                            // todo 这里解析可以判空，防止出现解析失败的情况，如果一个类型解析失败应该用另一个类型来解析
                            singleType = type
                            break
                        }
                    }
                }

                if(result.value != null) {
                    realAttrValue = result.value
                    "enum"
                } else {
                    // 不是引用、不是枚举
                    singleType
                }
            }
            val compiledAttrValue = attrValueCompiles[compileType]?.compile(realAttrValue, compiler)
            if (compiledAttrValue == null) {
                throw Exception("error $compileType $realAttrValue")
            }
            if (compiledAttrValue.stringValue != null) {
                compiler.addOtherString(compiledAttrValue.stringValue)
            }
            return Attribute.ResValue().apply {
                this.data = compiledAttrValue.data
                this.type = compiledAttrValue.type
                this.stringData = compiledAttrValue.stringValue
            }
        }
        return null
    }
}
