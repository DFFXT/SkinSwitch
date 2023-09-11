package com.example.viewdebug.ui.page.parser

import android.view.View
import com.example.viewdebug.ui.page.itemHanlder.Item
import com.example.viewdebug.util.ViewDebugInfo
import com.skin.skincore.collector.ViewUnion
import java.lang.ref.WeakReference

/**
 * 图片引用类型
 */
object ReferenceParser : Parser {
    override fun getItem(view: View, attrId: Int, attrName: String, viewUnion: ViewUnion?, viewDebugInfo: ViewDebugInfo?): Item? {
        val resId = viewUnion?.get(attrId)?.resId ?: return null

        val attrDescribe = attrName + "=@" + view.context.resources.getResourceTypeName(resId) + "/" + view.context.resources.getResourceEntryName(resId)
        val layoutId = viewDebugInfo?.layoutId
        val layoutName: String = if (layoutId != null && layoutId != 0) {
            view.context.resources.getResourceEntryName(layoutId)
        } else {
            view::class.java.simpleName
        }
        return Item(WeakReference(view), resId, layoutName, attrId, attrDescribe)
    }
}