package com.example.viewdebug.xml.struct.rule

import java.io.File

object RuleTest {
    @JvmStatic
    fun main(vararg args: String) {
        val file = File("attrs.xml")
        val parse = RuleParse("app")
        parse.parse(file.inputStream())
        println(parse.getValue("View", "layout_width", "wrap_content"))
        println(parse.getValue("View", "textColor", "是是是"))
    }
}
