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
    // 当data的值需要通过字符串常量池获取的话，则为true
    val parentValueValid: Boolean = false
)