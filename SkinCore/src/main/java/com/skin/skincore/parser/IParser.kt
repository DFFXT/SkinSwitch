package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.IAttrCollector
import com.skin.skincore.collector.ViewUnion

interface IParser {
    fun parse(parent: View?, view: View, attributeSet: AttributeSet): ViewUnion
}