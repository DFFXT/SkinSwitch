package com.example.viewdebug.ui.page.attribute

import android.content.Context
import android.view.View
import com.fxf.debugwindowlibaray.ui.UIPage

/**
 * 获取view信息
 */
internal interface Read<T : View> {
    fun getValue(view: T): CharSequence?

}

/**
 * View属性更新接口
 */
internal interface Update<T : View> : Read<T> {
    fun update(view: T, vararg args: String)
}

/**
 * 页面跳转
 */
internal interface Link<T: View>: Read<T> {
    fun onClick(host: UIPage)
}