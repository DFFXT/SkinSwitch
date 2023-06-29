package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import com.example.viewdebug.ui.UIPage

/**
 * 对外的view图片属性管理
 */
object ViewDebugImageManager {
    @SuppressLint("StaticFieldLeak")
    private var uiPage: ViewImageShowPage = ViewImageShowPage()
    internal fun getPage(): UIPage {
        return uiPage
    }

    fun addAttribute(id: Int, name: String) {
        uiPage.addAttribute(id, name)
    }

    fun removeAttribute(id: Int) {
        uiPage.removeAttribute(id)
    }
}
