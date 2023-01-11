package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.content.res.ResourcesCompat

/**
 * 换肤resource，通过当前主题加载对应皮肤包里面的资源
 * 目前仅支持 drawable、color
 * 需要其他类型的资源需要自己重写对应方法
 * todo 目前未实现theme的换肤
 */
class MergeResource(
    private var res: Resources,
    private var pkg: String,
    private val default: Resources
) : Resources(
    default.assets, default.displayMetrics, default.configuration
) {
    private var useDefault = false
    private var currentRes = res

    // region drawable、color重写
    override fun getDrawable(id: Int, theme: Theme?): Drawable? {
        try {
            if (useDefault) {
                return ResourcesCompat.getDrawable(currentRes, id, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = currentRes.getIdentifier(name, "drawable", pkg)
            return currentRes.getDrawable(skinPackId)
        } catch (e: Throwable) {
            return currentRes.getDrawable(id, theme)
        }
    }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        try {
            if (useDefault) {
                return default.getDrawableForDensity(id, density, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = res.getIdentifier(name, "drawable", pkg)
            return res.getDrawableForDensity(skinPackId, density, null)
        } catch (e: Throwable) {
            return default.getDrawableForDensity(id, density, theme)
        }
    }

    override fun getColor(id: Int, theme: Theme?): Int {
        try {
            if (useDefault) {
                return currentRes.getColor(id, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = currentRes.getIdentifier(name, "color", pkg)
            return currentRes.getColor(skinPackId, null)
        } catch (e: Throwable) {
            return currentRes.getColor(id, theme)
        }
    }

    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
        try {
            if (useDefault) {
                return currentRes.getColorStateList(id, theme)
            }
            val name = this.getResourceEntryName(id)
            val skinPackId = currentRes.getIdentifier(name, "color", pkg)
            return currentRes.getColorStateList(skinPackId, null)
        } catch (e: Throwable) {
            return currentRes.getColorStateList(id, theme)
        }
    }
    // endregion

    override fun getResourceEntryName(resid: Int): String {
        return default.getResourceEntryName(resid)
    }

    // region 同步Configuration，确保皮肤包和默认资源使用的是同一种配置
    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        super.updateConfiguration(config, metrics)
        default.updateConfiguration(config, metrics)
        res.updateConfiguration(config, metrics)
    }

    override fun getConfiguration(): Configuration {
        return currentRes.configuration
    }

    // endregion
    /**
     * 切换到默认资源
     */
    fun switchToDefault() {
        useDefault = true
        currentRes = default
    }

    /**
     * 设置皮肤包
     */
    fun setSkinTheme(res: Resources, pkg: String) {
        this.res = res
        this.pkg = pkg
        useDefault = false
        currentRes = res
    }
}
