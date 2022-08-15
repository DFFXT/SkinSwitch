package com.skin.skincore.apply

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.skin.skincore.collector.DefaultCollector

/**
 * View 控件样式apply
 */
open class ViewApply<T : View> : IApply<T> {
    override fun apply(view: View): Boolean = true
    override fun applyColor(view: T, attrName: String, color: Int?) {
        when (attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.background = ColorDrawable(color ?: Color.TRANSPARENT)
            }
        }
    }

    override fun applyStateColor(view: T, attrName: String, color: ColorStateList?) {
        /*when(attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.setBackgroundDrawable()
            }
        }*/
    }

    override fun applyDrawable(view: T, attrName: String, drawable: Drawable?) {
        when (attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.background = drawable
            }
        }
    }
}
