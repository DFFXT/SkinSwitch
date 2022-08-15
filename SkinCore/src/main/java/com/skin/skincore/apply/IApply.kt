package com.skin.skincore.apply

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View

/**
 * 属性应用
 */
interface IApply<in T : View> {
    /**
     * 是否能够被执行
     */
    fun apply(view: View): Boolean
    fun applyColor(view: T, attrName: String, color: Int?)
    fun applyStateColor(view: T, attrName: String, color: ColorStateList?)
    fun applyDrawable(view: T, attrName: String, drawable: Drawable?)
}
