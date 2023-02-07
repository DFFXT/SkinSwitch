package com.skin.skincore.apply

import android.util.SparseBooleanArray
import android.view.View
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.provider.IResourceProvider

/**
 * 换肤执分发器
 */
object AttrApplyManager {
    private val skinAttrStrategy = SparseBooleanArray()
    private val applySet: MutableList<IApply<View>> = mutableListOf<IApply<*>>(
        ImageViewApply(),
        TextViewApply(),
        ViewApply<View>()
    ) as MutableList<IApply<View>>

    fun apply(view: View, union: ViewUnion, provider: IResourceProvider) {
        // 当前策略不支持换肤
        if (!skinAttrStrategy.get(union.getSkinAtrValue())) return

        applySet.forEach { apply ->
            if (apply.apply(view)) {
                union.forEach {
                    apply.customApply(view, it.value.resourceType, it.value.attributeName, provider, it.value.resId)
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

    /**
     * 设置app:skin对应值的策略
     */
    fun setSkinAttrStrategy(skinAttrValue: Int, apply: Boolean) {
        skinAttrStrategy.put(skinAttrValue, apply)
    }
}
