package com.skin.skincore.apply

import android.view.View
import com.skin.skincore.collector.DefaultAttrCollector
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.getSkinTheme

/**
 * View 控件样式apply
 */
open class ViewApply<T : View> : IApply<T> {
    override fun apply(view: View): Boolean = true

    protected fun applyBackground(
        view: T,
        customResType: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (customResType) {
            ResType.DRAWABLE -> {
                view.background = provider.getDrawable(resId, view.context.getSkinTheme())
            }
            ResType.COLOR -> {
                view.setBackgroundColor(provider.getColor(resId, view.context.getSkinTheme()))
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
