package com.skin.skincore.collector

import android.view.View
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.parser.DefaultParser
import com.skin.skincore.parser.IParser

/**
 * 属性收集器
 */
class DefaultAttrCollector : IAttrCollector<View> {
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

    /**
     * @param id 属性id, 例如[android.R.attr.background]
     * @param name 名称，这个随意，只要不重复，一般取属性的名称，如：”background“、”app:tint“等
     */
    fun addSupportAttr(id: Int, name: String) {
        if (!attrMap.containsKey(id)) {
            attrMap[id] = name
        }
    }

    fun removeSupportAttr(id: Int) {
        attrMap.remove(id)
    }

    override val supportAttr: LinkedHashMap<Int, String> = attrMap
    override val parser: IParser = DefaultParser(AttrApplyManager.getSupportAttributeId().toIntArray())
}
