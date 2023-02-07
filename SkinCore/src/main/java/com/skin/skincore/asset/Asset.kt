package com.skin.skincore.asset

import android.content.res.Resources
import android.content.res.Resources.Theme

/**
 * 皮肤包（apk）解析的资源
 */
class Asset(
    val pkgName: String,
    val res: Resources
) {
    private val themeSet = HashMap<Int, Resources.Theme>()
    fun applyTheme(skinThemeId: Int): Theme {
        if (!themeSet.containsKey(skinThemeId)) {
            val theme = res.newTheme()
            theme.applyStyle(skinThemeId, true)
            themeSet[skinThemeId] = theme
        }
        return themeSet[skinThemeId]!!
    }
}
