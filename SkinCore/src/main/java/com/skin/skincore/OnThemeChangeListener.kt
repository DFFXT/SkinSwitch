package com.skin.skincore

import com.skin.skincore.apply.base.BaseViewApply

/**
 * 换肤监听
 */
interface OnThemeChangeListener {
    /**
     * @param theme 当前主题
     * @param isNight 当前主题白天黑夜模式
     * @param eventType 触发事件
     * [BaseViewApply.EVENT_TYPE_THEME]
     * [BaseViewApply.EVENT_TYPE_CREATE]
     * [BaseViewApply.EVENT_TYPE_ANY]
     * 也有可能是自定义事件
     */
    fun onThemeChanged(theme: Int, isNight: Boolean, eventType: IntArray)
}
