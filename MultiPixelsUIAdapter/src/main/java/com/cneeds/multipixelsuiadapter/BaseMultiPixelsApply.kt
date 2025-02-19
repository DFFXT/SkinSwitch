package com.cneeds.multipixelsuiadapter

import android.view.View

/**
 * 分辨率适配apply
 */
abstract class BaseMultiPixelsApply<T : View>(val supportAttribute: Int, val cls: Class<T>) {
    fun tryApply(
        view: View,
        resId: Int
    ): RunnableAfterInflate? {
        if (cls.isInstance(view)) {
            // val name = view.resources.getResourceName(resId)
            // 这里可以考虑对name进行判断，比如m_开头才继续处理
            return apply(view as T, resId)
        }
        return null
    }

    /**
     * 解析到对应属性时回调
     * @return Runnable? 返回一个runnable，会在inflate结束后回调，用于更新View的LayoutParams，因为执行到这里时view还没有layoutParams的
     */
    abstract fun apply(
        view: T,
        resId: Int
    ): RunnableAfterInflate?

    interface RunnableAfterInflate: Runnable
}