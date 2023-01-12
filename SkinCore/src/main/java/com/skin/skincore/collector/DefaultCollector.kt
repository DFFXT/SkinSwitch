package com.skin.skincore.collector

import android.view.View
import com.skin.skincore.parser.DefaultParser
import com.skin.skincore.parser.IParser

/**
 * 属性收集器
 */
class DefaultCollector : IAttrCollector<View> {
    private val attrMap = LinkedHashMap<Int, String>()

    companion object {
        const val ATTR_TEXT_COLOR = "textColor"
        const val ATTR_BACKGROUND = "background"
        const val ATTR_SRC = "src"
    }

    init {
        attrMap[android.R.attr.textColor] = ATTR_TEXT_COLOR
        attrMap[android.R.attr.background] = ATTR_BACKGROUND
        attrMap[android.R.attr.src] = ATTR_SRC
    }

    fun addSupportAttr(id: Int, name: String) {
        if (!attrMap.containsKey(id)) {
            attrMap[id] = name
        }
    }

    override val supportAttr: LinkedHashMap<Int, String> = attrMap
    override val parser: IParser = DefaultParser(attrMap)
}
