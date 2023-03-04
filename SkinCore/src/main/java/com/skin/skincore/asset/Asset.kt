package com.skin.skincore.asset

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import com.skin.skincore.collector.applyNight
import com.skin.skincore.collector.isNight
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 皮肤包（apk）解析的资源
 * @param application 应用application
 * @param pkgName 皮肤包包名
 * @param res 皮肤包资源
 */
class Asset(
    private val application: Application,
    private val skinPathProvider: ISkinPathProvider
) {
    private val resourceLoader: IResourceLoader = DefaultResourceLoader()
    lateinit var res: Resources
        private set
    val pkgName: String
    private val day: Resources by lazy {
        if (!res.isNight()) return@lazy res
        val info = resourceLoader.createAsset(application.baseContext, skinPathProvider)
        createResource(info, false)
    }
    private val night: Resources by lazy {
        if (res.isNight()) return@lazy res
        val info = resourceLoader.createAsset(application.baseContext, skinPathProvider)
        createResource(info, true)
    }

    init {
        val info = resourceLoader.createAsset(application.baseContext, skinPathProvider)
        pkgName = info.pkgName
        res = createResource(info, application.resources.isNight())
    }

    // 当前皮肤包包含的主题
    private val themeSet = HashMap<Int, Resources.Theme>()


    private var themeId = 0
    private lateinit var dayTheme: Theme
    private lateinit var nightTheme: Theme

    fun getTheme(): Theme {
        return if (res == day) dayTheme
        else nightTheme
    }

    fun applyTheme(skinThemeId: Int) {
        themeId = skinThemeId
        if (!themeSet.containsKey(skinThemeId)) {
            dayTheme = day.newTheme()
            dayTheme.applyStyle(skinThemeId, true)

            nightTheme = night.newTheme()
            nightTheme.applyStyle(skinThemeId, true)
            // themeSet[skinThemeId] = dayTheme
        }
        return
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
        // FIX AssetManager和Resource一样，都需要更新Configuration配置
        res.applyNight(isNight)
    }

    private fun createResource(assetInfo: AssetInfo, isNight: Boolean): Resources {
        val config = Configuration(application.resources.configuration)
        config.applyNight(isNight)
        // 这里创建了Resource对象，会更改AssetManger内部的Configuration，影响其他Resource持有的AssetManger
        // 所以需要一个Resource对应一个AssetManger
        return Resources(assetInfo.assetManager, application.resources.displayMetrics, config)
    }
}
