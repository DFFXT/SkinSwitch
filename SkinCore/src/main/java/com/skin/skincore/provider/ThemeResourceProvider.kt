package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * @param res 资源提供者
 * @param pkgName 资源包包名
 * @param default 主工程资源，用于确定id对应的名称
 * @param defaultProvider 当无法找到资源时，使用该提供器返回默认资源
 */
class ThemeResourceProvider(
    private val res: Resources,
    private val pkgName: String,
    private val default: Resources,
    private val defaultProvider: IResourceProvider
) :
    IResourceProvider {

    override fun getDefaultResourceProvider(): IResourceProvider {
        return defaultProvider
    }

    override fun getColor(resId: Int): Int {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, "color")
        if (id == 0) {
            return getDefaultResourceProvider().getColor(resId)
        }
        return res.getColor(id)
    }

    override fun getStateColor(resId: Int): ColorStateList {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, "drawable")
        if (id == 0) {
            return getDefaultResourceProvider().getStateColor(resId)
        }
        return res.getColorStateList(id)
    }

    override fun getDrawable(resId: Int): Drawable {
        val name = getResourceEntryName(resId)
        val id = getDelegateResourceId(name, default.getResourceTypeName(resId))
        if (id == 0) {
            return getDefaultResourceProvider().getDrawable(resId)
        }
        return res.getDrawable(id)
    }

    private fun getDelegateResourceId(name: String, type: String): Int {
        return res.getIdentifier(name, type, pkgName)
    }

    override fun getResourceEntryName(resId: Int): String {
        return getDefaultResourceProvider().getResourceEntryName(resId)
    }
}
