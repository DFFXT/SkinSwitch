package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 * 资源提供者
 */
interface IResourceProvider {
    /**
     * 获取兜底资源提供者
     */
    fun getDefaultResourceProvider(): IResourceProvider
    fun getColor(@ColorRes resId: Int, theme: Theme?): Int
    fun getStateColor(resId: Int, theme: Theme?): ColorStateList
    fun getDrawable(@DrawableRes resId: Int, theme: Theme?): Drawable

    fun getResourceEntryName(resId: Int): String

    /**
     * 获取当前皮肤的resource
     */
    fun getCurrentResource(): Resources
}
