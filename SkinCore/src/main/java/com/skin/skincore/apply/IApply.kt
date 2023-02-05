package com.skin.skincore.apply

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * 属性应用
 */
interface IApply<in T : View> {
    /**
     * @param view 适配的view
     * @param resType 自定义类型
     * @param attrName 需要变更的属性名称
     * @param provider 资源提供器，自定义资源类型需要自定义IResourceProvider使其支持对应类型
     * @param resId 资源id
     */
    fun customApply(view: T, @ResType.ResType resType: String, attrName: String, provider: IResourceProvider, resId: Int)

    fun apply(view: View): Boolean
}
