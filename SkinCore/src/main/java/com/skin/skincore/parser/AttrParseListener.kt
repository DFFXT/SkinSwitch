package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.collector.ViewUnion

/**
 * 属性解析监听
 */
interface AttrParseListener {
    fun onAttrParsed(parent: View?, view: View, attributeSet: AttributeSet, union: ViewUnion)

    fun onInflateFinish(rootView: View) {}
}