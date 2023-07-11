package com.example.viewdebug.xml

import android.content.Context
import com.example.viewdebug.xml.struct.rule.RuleParse

/**
 * 管理xml规则文件
 */
object AndroidXmRuleManager {
    private val parseAndroid = RuleParse("android")
    private val thirdParses = ArrayList<RuleParse>()
    fun init(ctx: Context) {
        parseAndroid.parse(ctx.assets.open("rules/android-attrs.xml"))

        val constraint = RuleParse("app")
        constraint.parse(ctx.assets.open("rules/constraint-values.xml"))
        val custom = RuleParse("")
        custom.parse(ctx.assets.open("rules/no-namespace-attr.xml"))
        thirdParses.add(constraint)
        thirdParses.add(custom)
    }

    fun getValue(tagName: String, attrName: String, attrValue: String, nsPrefix: String?): RuleParse.ValueData? {
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
