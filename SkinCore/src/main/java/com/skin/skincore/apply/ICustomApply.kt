package com.skin.skincore.apply

import android.view.View
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * 自定义资源类型接口
 * 目前仅支持颜色资源、图片资源，如果要支持其他资源，如：dimes尺寸资源、string字符串资源等
 */
interface ICustomApply<T : View> {
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
