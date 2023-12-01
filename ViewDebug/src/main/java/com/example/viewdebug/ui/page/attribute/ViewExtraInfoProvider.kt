package com.example.viewdebug.ui.page.attribute

import android.view.View

/**
 * View 支持哪些属性的变更
 */
internal abstract class ViewExtraInfoProvider<T : View> {
    /**
     * key：属性名称，用于显示；value：对应的属性更新处理器
     * value Read类型，只读
     *       Update类型，可更新数据
     */
    abstract val extraInfoProvider: LinkedHashMap<String, Read<T>>

    /**
     * 是否支持处理当前view
     */
    abstract fun support(view: View): Boolean


    fun update(view: View, attributeName: String, vararg args: String) {
        (extraInfoProvider[attributeName] as? Update)?.update(view as T, *args)
    }
}