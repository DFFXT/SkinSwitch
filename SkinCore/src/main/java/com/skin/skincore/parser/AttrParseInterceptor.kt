package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.collector.ViewUnion

/**
 * 视图解析拦截器
 */
interface AttrParseInterceptor {
    /**
     * @return true，后续不再走默认的属性解析，也不回调[afterParse]
     */
    fun beforeParse(parent: View?, view: View, attributeSet: AttributeSet): Boolean

    /**
     * 默认解析执行后的回调
     * @param union 解析后的属性集合
     */
    fun afterParse(parent: View?, view: View, attributeSet: AttributeSet, union: ViewUnion)
}