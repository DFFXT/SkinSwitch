package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.parser.Parser

/**
 * 对外的view图片属性管理
 */
object ViewDebugImageManager {
    @SuppressLint("StaticFieldLeak")
    private var uiPage: ViewImageShowPage = ViewImageShowPage()
    internal fun getPage(): UIPage {
        return uiPage
    }

    fun addAttribute(id: Int, name: String, parser: Parser) {
        uiPage.addAttribute(id, name, parser)
    }

    fun removeAttribute(id: Int) {
        uiPage.removeAttribute(id)
    }
}
