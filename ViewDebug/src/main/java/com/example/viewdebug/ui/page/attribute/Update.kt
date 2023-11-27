package com.example.viewdebug.ui.page.attribute

import android.view.View

/**
 * View属性更新接口
 */
internal interface Update<T : View>: Read<T> {
    fun update(view: T, vararg args: String)
}