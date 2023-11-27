package com.example.viewdebug.ui.page.attribute.impl

import android.widget.TextView
import com.example.viewdebug.ui.page.attribute.Read
import com.skin.skincore.collector.getViewUnion

/**
 * 获取文本颜色，如果没有对应资源文件，则返回颜色值
 */
internal class TextColorRead: Read<TextView>{
    override fun getValue(view: TextView): String {
        val colorId = view.getViewUnion()?.get(android.R.attr.textColor)?.resId
        if (colorId != null && colorId !=0) {
            return view.resources.getResourceEntryName(colorId)
        }
        return "#"+Integer.toHexString(view.currentTextColor)
    }

}