package com.example.viewdebug.xml.struct.writer.helper.value

import com.example.viewdebug.xml.struct.XmlCompiler

abstract class AttrValueCompile(val type: String) {
    /**
     * @return pair first: type ；second: data
     */
    abstract fun compile(attrValue: String, compiler: XmlCompiler): Pair<Byte, Int>?
}