package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
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
    private val defaultProvider: IResourceProvider,
) :
    IResourceProvider {
    override fun getColor(resId: Int?): Int? {
        val name = getResourceName(resId) ?: return defaultProvider.getColor(resId)
        val id = getDelegateResourceId(name, "color")
        return res.getColor(id)
    }

    override fun getStateColor(resId: Int?): ColorStateList? {
        val name = getResourceName(resId) ?: return defaultProvider.getStateColor(resId)
        val id = getDelegateResourceId(name, "drawable")
        return res.getColorStateList(id)
    }

    override fun getDrawable(resId: Int?): Drawable? {
        val name = getResourceName(resId) ?: return defaultProvider.getDrawable(resId)
        val id = getDelegateResourceId(name, "drawable")
        return res.getDrawable(id)
    }

    override fun getMipmap(resId: Int?): Drawable? {
        val name = getResourceName(resId) ?: return defaultProvider.getMipmap(resId)
        val id = getDelegateResourceId(name, "mipmap")
        return res.getDrawable(id)
    }

    private fun getDelegateResourceId(name: String, type: String): Int {
        return res.getIdentifier(name, type, pkgName)
    }

    private fun getResourceName(id: Int?): String? {
        id ?: return null
        return try {
            default.getResourceEntryName(id)
        } catch (notFoundException: NotFoundException) {
            null
        }
    }
}
