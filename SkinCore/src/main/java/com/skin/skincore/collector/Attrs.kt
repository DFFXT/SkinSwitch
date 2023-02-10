package com.skin.skincore.collector

import com.skin.skincore.collector.ResType.UNDEFINE

/**
 * @param resId 资源id，皮肤包中获取时需转换为name再通过name获取资源
 * @param attributeName 属性名称（textColor、src、background等）
 * @param resourceType 资源类型，颜色，图片等
 */
class Attrs(
    val resId: Int,
    val attributeId: Int,
    @ResType.ResType val resourceType: String = UNDEFINE
)
