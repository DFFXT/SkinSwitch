package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

class AttrEnumValueCompile: AttrValueCompile("enum") {
    override fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue? {
        val intEnum = attrValue.toIntOrNull()
        return if (intEnum != null) {
            CompiledAttrValue(ResourceType.TYPE_FIRST_INT, intEnum)
        } else {
            CompiledAttrValue(ResourceType.TYPE_STRING, compiler.addString(attrValue), true)
        }
    }

}