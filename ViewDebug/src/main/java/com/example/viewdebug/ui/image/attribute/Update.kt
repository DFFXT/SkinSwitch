package com.example.viewdebug.ui.image.attribute

import android.view.View

/**
 * View属性更新接口
 */
internal interface Update<T : View> {
    fun update(view: T, vararg args: String)

    fun getValue(view: T): String
}