package com.example.viewdebug.xml.struct.writer.helper

import com.example.viewdebug.xml.AndroidXmRuleManager
import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.Attribute
import com.example.viewdebug.xml.struct.writer.helper.value.AttReferenceValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrBooleanValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrDimensionValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrFloatValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrFractionValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrIntValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrTransValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrStringValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrValueCompile
import com.example.viewdebug.xml.struct.writer.helper.value.CompiledAttrValue
import com.skin.log.Logger
import java.lang.Exception

class AttributeWriterHelper(private val compiler: XmlCompiler) {
    private val attrValueCompiles = LinkedHashMap<String, AttrValueCompile>()
    private val specialCompile = AttrTransValueCompile()

    init {
        // 这些格式有着自己的优先级
        // 如果一个无法处理，需要考虑其它处理器
        addCompiler(AttReferenceValueCompile())
        addCompiler(AttrColorValueCompile())
        addCompiler(AttrFractionValueCompile())
        addCompiler(AttrDimensionValueCompile())
        addCompiler(AttrBooleanValueCompile())
        addCompiler(AttrIntValueCompile())
        addCompiler(AttrFloatValueCompile())
        addCompiler(AttrStringValueCompile())
        //addCompiler(AttrTransValueCompile())
    }

    fun addCompiler(compile: AttrValueCompile) {
        attrValueCompiles[compile.type] = compile
    }

    /**
     * 编译属性值
     */
    fun compileAttributeResValue(tagName: String, attrName: String, attrValue: String, nsPrefix: String?): Attribute.ResValue? {
        val result = AndroidXmRuleManager.getValue(tagName, attrName, attrValue, nsPrefix)
        if (result != null) {
            Logger.i("AttributeWriterHelper", "$tagName $attrName $attrValue type ${result.type}")
            var singleType = result.type
            val realAttrValue = result.value ?: attrValue

            var compiledAttrValue: CompiledAttrValue? = null
            if (result.value != null) {
                compiledAttrValue = specialCompile.compile(result.value, compiler)
            } else {
                // 所有的资源都有引用格式
                if (!singleType.contains(FormatType.TYPE_REFERENCE)) {
                    singleType.add(FormatType.TYPE_REFERENCE)
                }
                for (compile in attrValueCompiles) {
                    // 寻找能够处理的东西
                    if (singleType.contains(compile.key)) {
                        compiledAttrValue = compile.value.compile(attrValue, compiler)
                        if (compiledAttrValue != null) {
                            break
                        }
                    }
                }
            }



            /*val compileType = if (attrValue.startsWith("@")) {
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
            val compiledAttrValue = attrValueCompiles[compileType]?.compile(realAttrValue, compiler)*/
            if (compiledAttrValue == null) {
                throw Exception("error $singleType $realAttrValue")
            }
            compiledAttrValue.stringValue?.let {
                compiler.addOtherString(it)
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
