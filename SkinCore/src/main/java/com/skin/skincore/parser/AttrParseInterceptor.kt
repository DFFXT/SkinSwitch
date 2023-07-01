package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View

/**
 * 视图解析拦截器
 */
interface AttrParseInterceptor {
    /**
     * @return true，后续不再走默认的属性解析，也不回调解析监听，还会终止app:skin等属性的传播
     */
    fun beforeParse(parent: View?, view: View, attributeSet: AttributeSet): Boolean
}
