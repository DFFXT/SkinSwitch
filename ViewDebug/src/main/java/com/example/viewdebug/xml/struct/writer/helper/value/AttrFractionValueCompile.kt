package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.ReferenceType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ExternalFunction
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

class AttrFractionValueCompile : AttrValueCompile(FormatType.TYPE_FRACTION) {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        return if (attrValue.endsWith("%") || attrValue.endsWith("%p")) {
            // 前32位存储类型，后32位存储data
            val typeAndData = ExternalFunction.stringToFloat(attrValue)
            CompiledAttrValue((typeAndData shr 32).toByte(), (typeAndData and 0xFFFFFFFF).toInt())
        } else {
            null
        }
    }
}