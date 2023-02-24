package com.skin.skincore.collector

import com.skin.skincore.collector.ResType.Companion.UNDEFINE

/**
 * @param resId 资源id，皮肤包中获取时需转换为name再通过name获取资源
 * @param attributeId 属性id (android.R.attr.textColor等)
 * @param resourceType 资源类型，颜色，图片等
 * @param value 属性值
 */
class Attrs(
    val resId: Int,
    val attributeId: Int,
    @ResType val resourceType: String = UNDEFINE,
    val value: String? = null
)
