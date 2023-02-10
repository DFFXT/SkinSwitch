package com.skin.skincore.apply.base

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * @param supportAttribute 支持的属性
 * @param cls 限定的类名
 */
abstract class BaseViewApply<T : View>(val supportAttribute: Int, private val cls: Class<T>? = null) {
    /**
     * @param view 属性修改对象
     * @param resId 属性值id
     * @param resType 资源类型
     * @param provider 资源提供者
     * @param theme 主题
     */
    abstract fun apply(view: T, resId: Int, @ResType resType: String, provider: IResourceProvider, theme: Theme?)
}
