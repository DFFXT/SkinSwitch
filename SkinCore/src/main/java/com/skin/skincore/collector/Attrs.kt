package com.skin.skincore.collector

import androidx.annotation.StringDef

/**
 * @param resId 资源id，皮肤包中获取时需转换为name再通过name获取资源
 * @param attributeName 属性名称（textColor、src、background等）
 * @param resourceType 资源类型，颜色，图片等
 */
class Attrs(val resId: Int?, val attributeName: String, @ResType var resourceType: String = UNDEFINE) {
    companion object {
        const val COLOR = "color"
        const val DRAWABLE = "drawable"
        const val STATE_COLOR = "stateColor"
        const val UNDEFINE = "undefine"
    }

    @Target(
        AnnotationTarget.PROPERTY,
        AnnotationTarget.VALUE_PARAMETER
    )
    @StringDef(COLOR, DRAWABLE, STATE_COLOR, UNDEFINE)
    annotation class ResType
}
