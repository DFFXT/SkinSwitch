package com.example.viewdebug.ui.image.parser

import android.view.View
import com.example.viewdebug.ui.image.Item
import com.example.viewdebug.util.ViewDebugInfo
import com.skin.skincore.collector.ViewUnion

interface Parser {
    fun getItem(view: View, attrId: Int, attrName: String, viewUnion: ViewUnion?, viewDebugInfo: ViewDebugInfo?): Item?
}