package com.skin.skincore.apply.base

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * @param supportAttribute 支持的属性
 * @param eventType 事件类型，默认EVENT_TYPE_THEME，即皮肤切换、白天黑夜切换
 * @param cls 限定的类名，只有当view是cls代表的子类时才执行，否则会产生强制转换异常
 */
abstract class BaseViewApply<T : View>(
    val supportAttribute: Int,
    private val cls: Class<T>? = null,
    private val eventType: Int = EVENT_TYPE_THEME
) {

    companion object {
        // 任意事件，所有apply均需要响应
        const val EVENT_TYPE_ANY = 0

        // view第一次创建
        const val EVENT_TYPE_CREATE = 2

        // 皮肤切换
        const val EVENT_TYPE_THEME = 1
    }

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

    /**
     * 视图被创建时是否需要执行，默认执行
     */
    protected open fun isApplyWhenCreate(): Boolean = true

    internal fun tryApply(
        eventType: IntArray,
        view: View,
        resId: Int,
        @ResType resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        // 事件匹配
        if ((
            this.eventType == EVENT_TYPE_ANY || // 匹配任意事件
                eventType.contains(this.eventType) || // 匹配当前事件
                eventType.contains(EVENT_TYPE_CREATE) && isApplyWhenCreate() // 匹配创建事件
            ) &&
            // 类型匹配
            (cls == null || cls.isInstance(view))
        ) {
            apply(view as T, resId, resType, provider, theme)
        }
    }
}
