package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.writer.helper.ResourceType

class AttrEnumValueCompile: AttrValueCompile("enum") {
    override fun compile(attrValue: String, compiler: XmlCompiler): Pair<Byte, Int>? {
        val intEnum = attrValue.toIntOrNull()
        return if (intEnum != null) {
            Pair(ResourceType.TYPE_FIRST_INT, intEnum)
        } else {
            Pair(ResourceType.TYPE_STRING, compiler.addString(attrValue))
        }
    }

}