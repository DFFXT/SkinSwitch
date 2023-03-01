package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.skin.skincore.asset.Asset

/**
 * @param res 资源提供者
 * @param pkgName 资源包包名
 * @param default 主工程资源，用于确定id对应的名称
 * @param defaultProvider 当无法找到资源时，使用该提供器返回默认资源
 */
class ThemeResourceProvider(
    private val asset: Asset,
    private val default: Resources,
    private val defaultProvider: IResourceProvider
) :
    IResourceProvider {

    private val res: Resources
        get() = asset.res

    override fun getDefaultResourceProvider(): IResourceProvider {
        return defaultProvider
    }

    override fun getColor(resId: Int, theme: Theme?): Int {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, default.getResourceTypeName(resId))
        if (id == 0) {
            return getDefaultResourceProvider().getColor(resId, theme)
        }
        return ResourcesCompat.getColor(res, id, theme)
    }

    override fun getStateColor(resId: Int, theme: Theme?): ColorStateList {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, default.getResourceTypeName(resId))
        if (id == 0) {
            return getDefaultResourceProvider().getStateColor(resId, theme)
        }
        return ResourcesCompat.getColorStateList(res, id, theme)!!
    }

    override fun getDrawable(resId: Int, theme: Theme?): Drawable {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, default.getResourceTypeName(resId))
        if (id == 0) {
            return getDefaultResourceProvider().getDrawable(resId, theme)
        }
        return ResourcesCompat.getDrawable(res, id, theme)!!
    }

    private fun getDelegateResourceId(name: String, type: String): Int {
        return res.getIdentifier(name, type, asset.pkgName)
    }

    override fun getResourceEntryName(resId: Int): String {
        return getDefaultResourceProvider().getResourceEntryName(resId)
    }

    override fun getCurrentResource(): Resources = res
}
