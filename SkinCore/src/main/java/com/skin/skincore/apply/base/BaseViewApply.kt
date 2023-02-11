package com.skin.skincore.apply.base

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * @param supportAttribute 支持的属性
 * @param cls 限定的类名，只有当view是cls代表的子类时才执行，否则会产生强制转换异常
 */
abstract class BaseViewApply<T : View>(
    val supportAttribute: Int,
    private val cls: Class<T>? = null
) {
    /**
     * @param view 属性修改对象
     * @param resId 属性值id
     * @param resType 资源类型
     * @param provider 资源提供者
     * @param theme 主题
     */
    protected abstract fun apply(
        view: T,
        resId: Int,
        @ResType resType: String,
        provider: IResourceProvider,
        theme: Theme?
    )

    internal fun tryApply(
        view: View,
        resId: Int,
        @ResType resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        if (cls == null || cls.isInstance(view)) {
            apply(view as T, resId, resType, provider, theme)
        }
    }
}
