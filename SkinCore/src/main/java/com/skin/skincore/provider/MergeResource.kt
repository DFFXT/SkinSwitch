package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import com.skin.skincore.asset.IAsset

/**
 * 换肤resource，通过当前主题加载对应皮肤包里面的资源
 * 目前仅支持 drawable、color
 * 需要其他类型的资源需要自己重写对应方法
 * todo 目前未实现theme的换肤
 */
open class MergeResource(
    var asset: IAsset,
    val default: Resources,
    private var themeId: Int,
) : Resources(
    default.assets,
    default.displayMetrics,
    default.configuration,
) {
    private val res: Resources
        get() = asset.getResource()
    private val pkg: String = asset.pkgName()
    var useDefault = false
        private set
    private val currentRes: Resources
        get() = asset.getResource()

    // todo 优化MergeResource
    val theme: Theme?
        get() = asset.getTheme()

    init {
        // theme 同步
        applyThemeStyle(themeId)
    }

    /**
     * 设置主题样式
     */
    open fun applyThemeStyle(themeId: Int) {
        this.themeId = themeId
        // FIX 获取style必须要确定包名
        // com.incall.navi.bl:style/ActivityTranslucent
        // android:style/Theme.DeviceDefault.Light.DarkActionBar
        if (themeId == 0) return
        val themeName = if (default.getResourcePackageName(themeId) != pkg) {
            // 一般是android包名
            default.getResourceName(themeId)
        } else {
            // 项目包名，则不保留包名
            default.getResourceEntryName(themeId)
        }
        val skinThemeId = res.getIdentifier(themeName, default.getResourceTypeName(themeId), pkg)
        asset.applyTheme(skinThemeId)

        val theme = newTheme()
        theme.applyStyle(themeId, true)
    }

    // 这里需要对每个getDrawable进行重写，因为低版本上各个方法没有收束到一个方法（至少25没有）
    @Deprecated("Deprecated in Java", ReplaceWith("getDrawable(id, null)"))
    override fun getDrawable(id: Int): Drawable? {
        return getDrawable(id, null)
    }

    // region drawable、color重写
    override fun getDrawable(id: Int, theme: Theme?): Drawable? {
        // 经过分析sdk，Android 26上就解决了这个问题，25及以下需要额外适配
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 低版本上density不能为0，因为内部没有判断，导致0作为除数
            getDrawableForDensity(id, default.displayMetrics.density.toInt(), theme)
        } else {
            getDrawableForDensity(id, 0 ,theme)
        }
    }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        try {
            if (useDefault) {
                return default.getDrawableForDensity(id, density, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = res.getIdentifier(name, getResourceTypeName(id), pkg)
            return res.getDrawableForDensity(skinPackId, density, this.theme)
        } catch (e: Throwable) {
            return default.getDrawableForDensity(id, density, theme)
        }
    }

    override fun getColor(id: Int, theme: Theme?): Int {
        try {
            if (useDefault) {
                return default.getColor(id, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = currentRes.getIdentifier(name, getResourceTypeName(id), pkg)
            return currentRes.getColor(skinPackId, this.theme)
        } catch (e: Throwable) {
            return default.getColor(id, theme)
        }
    }

    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
        try {
            if (useDefault) {
                return default.getColorStateList(id, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = currentRes.getIdentifier(name, getResourceTypeName(id), pkg)
            return currentRes.getColorStateList(skinPackId, null)
        } catch (e: Throwable) {
            return default.getColorStateList(id, theme)
        }
    }
    // endregion

    override fun getResourceEntryName(resid: Int): String {
        return default.getResourceEntryName(resid)
    }

    // region 同步Configuration，确保皮肤包和默认资源使用的是同一种配置
    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        super.updateConfiguration(Configuration(config), metrics)
        default.updateConfiguration(Configuration(config), metrics)
        res.updateConfiguration(Configuration(config), metrics)
    }

    override fun getConfiguration(): Configuration {
        return currentRes.configuration
    }

    // endregion
    /**
     * 切换到默认资源
     */
    open fun switchToDefault() {
        useDefault = true
        // currentRes = default
    }

    /**
     * 设置皮肤包
     */
    open fun setSkinTheme(asset: IAsset) {
        this.asset = asset
        useDefault = false
        // currentRes = res
        applyThemeStyle(themeId)
    }
}
