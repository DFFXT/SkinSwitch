package com.skin.skincore.asset

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
    private val context: Context,
    private val skinPathProvider: ISkinPathProvider,
) : IAsset() {
    private val application = context.applicationContext ?: context
    private val configuration = context.resources.configuration
    private val displayMetrics = context.resources.displayMetrics
    private val resourceLoader: IResourceLoader = DefaultAssetLoader()
    private val info = resourceLoader.createAsset(application, configuration, skinPathProvider)

    /**
     * 存有一个问题，当创建一个白天的resources，然后立即创建一个黑夜的resources，再创建一个白天的resources，最后这个resources异常，显示为黑夜
     */
    private val day: Resources by lazy {
        val info = resourceLoader.createAsset(application, configuration, skinPathProvider)
        createResource(info, false)
    }
    private val night: Resources by lazy {
        val info = resourceLoader.createAsset(application, configuration, skinPathProvider)
        createResource(info, true)
    }
    var res: Resources = if (context.resources.isNight()) night else day
        private set

    init {
        val f= 0
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
        // val dm = res.displayMetrics
        res = if (isNight) {
            night
        } else {
            day
        }
        // res.displayMetrics.setTo(dm)
        // FIX AssetManager和Resource一样，都需要更新Configuration配置
        res.applyNight(isNight)
    }

    override fun getResource(): Resources {
        return res
    }

    override fun updateDisplayMetrics(update: (DisplayMetrics) -> Unit) {
        // update(res.displayMetrics)
        /*update(day.displayMetrics)
        update(night.displayMetrics)*/
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
         * updateConfiguration会导致density等数据重置
         * AndroidAutoSize库是对displayMetrics进行的修改，不完全合理
         * 正常情况下不应该直接修复displayMetrics，而是修改configuration
         * 此处不兼容AndroidAutoSize，要使用请通过AutoSizeConfig.getInstance().setOnAdaptListener()来监听displayMetrics的变化，同时手动更新configuration
         *
         * 存在AndroidAutoSize插件先修改displayMetrics的情况，此处需要更新displayMetrics
         */

        return Resources(assetInfo.assetManager, displayMetrics, config).apply {
            this.displayMetrics.setTo(this@Asset.displayMetrics)
        }
    }
}
