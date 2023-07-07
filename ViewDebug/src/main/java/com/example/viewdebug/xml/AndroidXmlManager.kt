package com.example.viewdebug.xml

import android.content.Context
import com.example.viewdebug.xml.struct.rule.RuleParse

object AndroidXmlManager {
    val parseAndroid = RuleParse("android")
    val thirdParses = ArrayList<RuleParse>()
    fun init(ctx: Context) {
        parseAndroid.parse(ctx.assets.open("rules/android-attrs.xml"))
        val constraint = RuleParse("app")
        constraint.parse(ctx.assets.open("rules/constraint-values.xml"))
        thirdParses.add(constraint)
    }

    fun getValue(tagName: String, attrName: String, attrValue: String, nsPrefix: String): RuleParse.ValueData? {
        if (parseAndroid.nsPrefix == nsPrefix) {
            return parseAndroid.getValue(tagName, attrName, attrValue)
        } else {
            for (parser in thirdParses) {
                return parser.getValue(tagName, attrName, attrValue) ?: continue
            }
        }
        return null
    }
}
