package com.example.viewdebug.xml

import android.content.Context
import com.example.viewdebug.xml.struct.rule.RuleParse
import com.skin.log.Logger
import java.io.File

/**
 * 管理xml规则文件
 * todo 支持自定义属性
 * 经观察，发现三方定义的属性都在build\intermediates\incremental\mergeDebugResources\merger.xml文件中
 * 如果在编译过程中将该文件复制到assets目录，那么就能支持了
 * 针对无法快速复制问题，推荐开发as插件，通过adb直接push到模拟器，模拟器通过监听文件来获取pc内容
 */
object AndroidXmRuleManager {
    private val parseAndroid = RuleParse("android")
    private val thirdParses = RuleParse("app")
    fun init(ctx: Context) {
        parseAndroid.parse(ctx.assets.open("rules/android-attrs.xml"))

        thirdParses.parse(ctx.assets.open("rules/constraint-values.xml"))
        thirdParses.parse(ctx.assets.open("rules/no-namespace-attr.xml"))
    }

    /**
     * 添加其它规则
     */
    fun addRuleFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            thirdParses.parse(file.inputStream())
        } else {
            Logger.e("AndroidXmRuleManager", "FileNotFound $filePath")
        }
    }

    fun getValue(tagName: String, attrName: String, attrValue: String, nsPrefix: String?): RuleParse.ValueData? {
        return if (parseAndroid.nsPrefix == nsPrefix) {
            parseAndroid.getValue(tagName, attrName, attrValue)
        } else {
            thirdParses.getValue(tagName, attrName, attrValue)
        }
    }
}
