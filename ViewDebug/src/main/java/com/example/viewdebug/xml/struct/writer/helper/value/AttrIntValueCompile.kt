package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.FormatType
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

class AttrIntValueCompile : AttrValueCompile(FormatType.TYPE_INT) {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        val intEnum = attrValue.toIntOrNull()
        if (intEnum != null) {
            return CompiledAttrValue(ResourceType.TYPE_FIRST_INT, intEnum)
        }
        return null
    }

}