package com.skin.skincore.asset

import android.content.res.Resources
import android.content.res.Resources.Theme

/**
 * 资源实体接口
 */
interface IAsset {
    val pkgName: String
    fun getTheme(): Theme?
    fun applyTheme(themeId: Int)
    fun applyNight(isNight: Boolean)
    fun getResource(): Resources
}
