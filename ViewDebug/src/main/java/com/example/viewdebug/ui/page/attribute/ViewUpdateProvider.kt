package com.example.viewdebug.ui.page.attribute

import android.view.View

/**
 * View 支持哪些属性的变更
 */
internal abstract class ViewUpdateProvider<T : View> {
    /**
     * key：属性名称，用于显示；value：对应的属性更新处理器
     */
    abstract val update: LinkedHashMap<String, Update<T>>

    /**
     * 是否支持处理当前view
     */
    abstract fun support(view: View): Boolean

    fun getSupportedAttribute(): List<String> {
        return update.map { it.key }
    }

    fun update(view: View, attributeName: String, vararg args: String) {
        update[attributeName]?.update(view as T, *args)
    }
}