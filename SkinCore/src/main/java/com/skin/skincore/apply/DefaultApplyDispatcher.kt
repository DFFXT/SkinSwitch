package com.skin.skincore.apply

import android.view.View
import com.skin.skincore.collector.Attrs
import com.skin.skincore.provider.IResourceProvider

class DefaultApplyDispatcher(provider: IResourceProvider) {
    private var currentProvider: IResourceProvider = provider
    private val apply: List<IApply<View>> = listOf<IApply<*>>(
        ImageViewApply(),
        TextViewApply(),
        ViewApply<View>()
    ) as List<IApply<View>>

    fun apply(view: View, attrs: Attrs) {
        apply.forEach {
            if (it.apply(view)) {
                when (attrs.resType) {
                    Attrs.Drawable -> {
                        it.applyDrawable(view, attrs.name, currentProvider.getDrawable(attrs.resId))
                    }
                    Attrs.COLOR -> {
                        it.applyColor(view, attrs.name, currentProvider.getColor(attrs.resId))
                    }
                    Attrs.STATE_COLOR -> {
                        it.applyStateColor(view, attrs.name, currentProvider.getStateColor(attrs.resId))
                    }
                }
            }
        }
    }

    fun switchProvider(provider: IResourceProvider) {
        currentProvider = provider
    }
}
