package com.example.viewdebug.ui.skin

import android.content.res.Resources
import android.content.res.Resources.Theme
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile

/**
 * values资源解码
 */
object ResourceDecode {
    /**
     * 获取颜色
     */
    fun getColor(resources: Resources, color: String, theme: Theme?) :Int? {
        val result = AttrColorValueCompile().getColor(color)
        if (result != null) {
            return result.data
        }
        val colorId = referenceDecode(resources, color)
        if (colorId != null) {
            return resources.getColor(colorId, theme)
        }
        return null
    }

    /**
     * 解析引用类型数据，返回引用id
     */
    private fun referenceDecode(resources: Resources, ref: String): Int? {
        if (ref.startsWith("@")) {
            val index = ref.indexOf('/')
            if (index > 0) {
                var type: String
                var pkgName: String
                // 系统资源
                if (ref.startsWith("@android:")) {
                    type = ref.substring(ref.indexOf(':') + 1, index)
                    pkgName = "android"
                } else {
                    type = ref.substring(1, index)
                    pkgName = ViewDebugInitializer.ctx.packageName
                }
                val name = ref.substring(index + 1)
                // 可能是系统资源
                val id = resources.getIdentifier(name, type, pkgName)
                if (id != 0) {
                    return id
                }
            }
        }
        return null
    }
}