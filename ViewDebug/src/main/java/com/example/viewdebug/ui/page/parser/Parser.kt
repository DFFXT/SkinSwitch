package com.example.viewdebug.ui.page.parser

import android.view.View
import com.example.viewdebug.ui.page.itemHanlder.Item
import com.example.viewdebug.util.ViewDebugInfo
import com.skin.skincore.collector.ViewUnion

interface Parser {
    fun getItem(view: View, attrId: Int, attrName: String, viewUnion: ViewUnion?, viewDebugInfo: ViewDebugInfo?): Item?
}