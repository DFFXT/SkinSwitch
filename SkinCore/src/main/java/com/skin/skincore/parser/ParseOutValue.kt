package com.skin.skincore.parser

import com.skin.skincore.collector.Attrs

class ParseOutValue {
    lateinit var attrs: List<Attrs>
    var skinAttrValue: Int = SKIN_ATTR_UNDEFINE

    companion object {
        const val SKIN_ATTR_FALSE = 0
        const val SKIN_ATTR_TRUE = 1
        const val SKIN_ATTR_UNDEFINE = 2
    }
}
