package com.skin.skincore.collector

import androidx.annotation.StringDef

object ResType {
    const val COLOR = "color"
    const val DRAWABLE = "drawable"
    const val MIPMAP = "mipmap"
    const val DIMENS = "dimens"
    const val UNDEFINE = "undefine"

    @Target(
        AnnotationTarget.PROPERTY,
        AnnotationTarget.VALUE_PARAMETER
    )
    @StringDef(COLOR, DRAWABLE, MIPMAP, DIMENS, UNDEFINE)
    annotation class ResType
}
