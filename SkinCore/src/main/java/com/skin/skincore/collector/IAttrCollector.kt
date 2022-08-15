package com.skin.skincore.collector

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.parser.IParser

/**
 * 属性收集器接口
 */
interface IAttrCollector<T : View> {
    // fun collect(view: View, attributeSet: AttributeSet)

    /**
     * 获取支持的属性
     * key 属性 (R.attr.textColor)
     * value 类型 (textColor)
     */
    val supportAttr: LinkedHashMap<Int, String>

    /**
     * 属性执行
     */
    val parser: IParser
}
