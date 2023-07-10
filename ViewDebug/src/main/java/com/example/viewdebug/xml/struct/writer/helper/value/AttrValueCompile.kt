package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.XmlCompiler

abstract class AttrValueCompile(val type: String) {
    /**
     * @return pair first: type ；second: data
     */
    abstract fun compile(attrValue: String, compiler: XmlCompiler): CompiledAttrValue?
}

class CompiledAttrValue(
    val type: Byte,
    val data: Int,
    // 当前数据为字符串
    val stringValue: String? = null
)