package com.skin.skincore.asset

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import com.skin.skincore.collector.applyNight
import com.skin.skincore.collector.isNight

/**
 * 皮肤包（apk）解析的资源
 * @param application 应用application
 * @param pkgName 皮肤包包名
 * @param res 皮肤包资源
 */
class Asset(
    private val application: Application,
    val pkgName: String,
    var res: Resources
) {
    private val day: Resources by lazy {
        if (!res.isNight()) return@lazy res
        createResource(false)
    }
    private val night: Resources by lazy {
        if (res.isNight()) return@lazy res
        createResource(true)
    }

    // 当前皮肤包包含的主题
    private val themeSet = HashMap<Int, Resources.Theme>()
    fun applyTheme(skinThemeId: Int): Theme {
        if (!themeSet.containsKey(skinThemeId)) {
            val theme = res.newTheme()
            theme.applyStyle(skinThemeId, true)
            themeSet[skinThemeId] = theme
        }
        return themeSet[skinThemeId]!!
    }

    /**
     * 更新皮肤包白天黑夜
     */
    fun applyNight(isNight: Boolean) {
        res = if (isNight) {
            night
        } else {
            day
        }
    }

    private fun createResource(isNight: Boolean): Resources {
        val config = Configuration(application.resources.configuration)
        config.applyNight(isNight)
        return application.createConfigurationContext(config).resources
    }
}
