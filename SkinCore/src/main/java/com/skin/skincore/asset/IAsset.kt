package com.skin.skincore.asset

import android.content.res.Resources
import android.content.res.Resources.Theme

/**
 * 资源实体接口
 */
abstract class IAsset {
    lateinit var assetKey: AssetLoaderManager.AssetKey
    abstract fun pkgName(): String
    abstract fun getTheme(): Theme?
    abstract fun applyTheme(themeId: Int)
    abstract fun applyNight(isNight: Boolean)
    abstract fun getResource(): Resources
}
