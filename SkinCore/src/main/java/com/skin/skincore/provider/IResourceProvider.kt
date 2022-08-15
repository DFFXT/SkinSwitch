package com.skin.skincore.provider

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

/**
 * 资源提供者
 */
interface IResourceProvider {
    fun getColor(resId: Int?): Int?
    fun getStateColor(resId: Int?): ColorStateList?
    fun getDrawable(resId: Int?): Drawable?
}
