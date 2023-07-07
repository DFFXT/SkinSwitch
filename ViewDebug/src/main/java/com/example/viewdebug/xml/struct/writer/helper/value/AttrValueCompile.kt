package com.example.viewdebug.xml.struct.writer.helper.value

abstract class AttrValueCompile(val type: String) {
    /**
     * @return pair first: type ；second: data
     */
    abstract fun compile(attrValue: String): Pair<Byte, Int>?
}