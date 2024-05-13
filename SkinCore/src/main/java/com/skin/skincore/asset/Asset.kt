package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.util.DisplayMetrics
import com.skin.log.Logger
import com.skin.skincore.collector.applyNight
import com.skin.skincore.collector.isNight
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 皮肤包（apk）解析的资源
 * @param context 对应要换肤的context
 * @param skinPathProvider 皮肤包配置
 * todo Asset的释放
 */
open class Asset(
    context: Context,
    private val skinPathProvider: ISkinPathProvider,
) : IAsset() {
    private val application = context.applicationContext ?: context
    private val configuration = context.resources.configuration
    private val displayMetrics = context.resources.displayMetrics
    private val resourceLoader: IResourceLoader = DefaultAssetLoader()
    private val info = resourceLoader.createAsset(application, configuration, skinPathProvider)
    lateinit var res: Resources
        private set
    private val day: Resources by lazy {
        if (!res.isNight()) return@lazy res
        val info = resourceLoader.createAsset(application, configuration, skinPathProvider)
        createResource(info, false)
    }
    private val night: Resources by lazy {
        if (res.isNight()) return@lazy res
        val info = resourceLoader.createAsset(application, configuration, skinPathProvider)
        createResource(info, true)
    }

    init {
        res = createResource(info, application.resources.isNight())
    }

    // 当前皮肤包包含的主题
    private val themeSet = HashMap<Int, Resources.Theme>()

    private var themeId = 0
    private lateinit var dayTheme: Theme
    private lateinit var nightTheme: Theme

    override fun pkgName(): String = info.pkgName
    override fun getTheme(): Theme? {
        if (res == day) {
            return if (this::dayTheme.isInitialized) return dayTheme else null
        } else {
            return if (this::nightTheme.isInitialized) return nightTheme else null
        }
    }

    override fun applyTheme(skinThemeId: Int) {
        themeId = skinThemeId
        if (!themeSet.containsKey(skinThemeId)) {
            if (!this::dayTheme.isInitialized) {
                dayTheme = day.newTheme()
            }
            dayTheme.applyStyle(skinThemeId, true)

            if (!this::nightTheme.isInitialized) {
                nightTheme = night.newTheme()
            }
            nightTheme.applyStyle(skinThemeId, true)
            // themeSet[skinThemeId] = dayTheme
        }
        return
    }

    /**
     * 更新皮肤包白天黑夜
     */
    override fun applyNight(isNight: Boolean) {
        Logger.d("Asset", "applyNight $isNight")
        res = if (isNight) {
            night
        } else {
            day
        }
        // FIX AssetManager和Resource一样，都需要更新Configuration配置
        res.applyNight(isNight)
    }

    override fun getResource(): Resources {
        return res
    }

    override fun updateDisplayMetrics(update: (DisplayMetrics) -> Unit) {
        update(day.displayMetrics)
        update(night.displayMetrics)
    }

    protected open fun createResource(assetInfo: AssetInfo, isNight: Boolean): Resources {
        val config = Configuration(configuration)
        config.applyNight(isNight)
        // 这里创建了Resource对象，会更改AssetManger内部的Configuration，影响其他Resource持有的AssetManger
        // 所以需要一个Resource对应一个AssetManger
        /**
         * 经查看源码（ResourcesImpl.java）,有个判断densityDpi的代码，会重置density数据，所以需要在创建后重新赋值
         *                 if (mConfiguration.densityDpi != Configuration.DENSITY_DPI_UNDEFINED) {
         *                     mMetrics.densityDpi = mConfiguration.densityDpi;
         *                     mMetrics.density =
         *                             mConfiguration.densityDpi * DisplayMetrics.DENSITY_DEFAULT_SCALE;
         *                 }
         */
        return Resources(assetInfo.assetManager, displayMetrics, config).apply {
            this.displayMetrics.density = this@Asset.displayMetrics.density
            this.displayMetrics.scaledDensity = this@Asset.displayMetrics.scaledDensity
        }
    }
}
