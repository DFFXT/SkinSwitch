package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable

/**
 * 资源提供者
 */
interface IResourceProvider {
    fun getDefaultResourceProvider(): IResourceProvider
    fun getColor(resId: Int, theme: Theme?): Int
    fun getStateColor(resId: Int, theme: Theme?): ColorStateList
    fun getDrawable(resId: Int, theme: Theme?): Drawable

    fun getResourceEntryName(resId: Int): String
}
