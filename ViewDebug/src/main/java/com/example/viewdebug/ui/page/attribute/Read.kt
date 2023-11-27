package com.example.viewdebug.ui.page.attribute

import android.view.View

/**
 * 获取view信息
 */
internal interface Read<T : View> {
    fun getValue(view: T): String

}