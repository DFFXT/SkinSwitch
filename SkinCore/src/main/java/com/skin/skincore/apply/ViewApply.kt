package com.skin.skincore.apply

import android.view.View
import com.skin.skincore.collector.DefaultAttrCollector
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * View 控件样式apply
 */
open class ViewApply<T : View> : IApply<T> {
    override fun apply(view: View): Boolean = true
    /*override fun applyColor(view: T, attrName: String, color: Int?) {
        when (attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.background = ColorDrawable(color ?: Color.TRANSPARENT)
            }
        }
    }

    override fun applyStateColor(view: T, attrName: String, color: ColorStateList?) {
        *//*when(attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.setBackgroundDrawable()
            }
        }*//*
    }

    override fun applyDrawable(view: T, attrName: String, drawable: Drawable?) {
        when (attrName) {
            DefaultCollector.ATTR_BACKGROUND -> {
                view.background = drawable
            }
        }
    }*/

    protected fun applyBackground(
        view: T,
        customResType: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (customResType) {
            ResType.DRAWABLE -> {
                view.background = provider.getDrawable(resId)
            }
            ResType.COLOR -> {
                view.setBackgroundColor(provider.getColor(resId))
            }
        }
    }

    override fun customApply(
        view: T,
        resType: String,
        attrName: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (attrName) {
            DefaultAttrCollector.ATTR_BACKGROUND -> {
                applyBackground(view, resType, provider, resId)
            }
        }
    }
}
