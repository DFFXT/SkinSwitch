package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.IAttrCollector

interface IParser {
    fun parse(view: View, attributeSet: AttributeSet, outValue: ParseOutValue)
}