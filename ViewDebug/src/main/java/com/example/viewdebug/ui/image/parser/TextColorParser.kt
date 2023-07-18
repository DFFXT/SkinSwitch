package com.example.viewdebug.ui.image.parser

import android.view.View
import android.widget.TextView
import com.example.viewdebug.R
import com.example.viewdebug.ui.image.Item
import com.example.viewdebug.util.ViewDebugInfo
import com.skin.skincore.collector.ViewUnion
import java.lang.ref.WeakReference

/**
 * TextColor引用类型
 */
object TextColorParser : Parser {
    override fun getItem(view: View, attrId: Int, attrName: String, viewUnion: ViewUnion?, viewDebugInfo: ViewDebugInfo?): Item? {
        if (view !is TextView) return null

        val attrDescribe = "text=" + view.text
        val layoutId = viewDebugInfo?.layoutId
        val layoutName: String = if (layoutId != null && layoutId != 0) {
            view.context.resources.getResourceEntryName(layoutId)
        } else {
            view::class.java.simpleName
        }
        return Item(WeakReference(view), R.mipmap.view_debug_text_view_type_icon, layoutName, attrId, attrDescribe)
    }
}