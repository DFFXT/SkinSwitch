package com.skin.skincore.apply

import android.view.View
import com.skin.skincore.collector.Attrs
import com.skin.skincore.provider.IResourceProvider

/**
 * 换肤执分发器
 */
object AttrApplyManager {
    private val applySet: MutableList<IApply<View>> = mutableListOf<IApply<*>>(
        ImageViewApply(),
        TextViewApply(),
        ViewApply<View>()
    ) as MutableList<IApply<View>>

    fun apply(view: View, attrs: Attrs, provider: IResourceProvider) {
        applySet.forEach {
            if (it.apply(view)) {
                when (attrs.resourceType) {
                    Attrs.DRAWABLE -> {
                        it.applyDrawable(view, attrs.attributeName, provider.getDrawable(attrs.resId))
                    }
                    Attrs.COLOR -> {
                        it.applyColor(view, attrs.attributeName, provider.getColor(attrs.resId))
                    }
                    Attrs.STATE_COLOR -> {
                        it.applyStateColor(view, attrs.attributeName, provider.getStateColor(attrs.resId))
                    }
                }
            }
        }
    }

    /**
     * 新增其他处理器
     */
    fun <T : View> addViewApply(apply: IApply<T>) {
        applySet.add(apply as IApply<View>)
    }
}
