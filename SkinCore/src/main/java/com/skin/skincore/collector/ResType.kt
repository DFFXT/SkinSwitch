package com.skin.skincore.collector

import androidx.annotation.StringDef
import com.skin.skincore.collector.ResType.Companion.COLOR
import com.skin.skincore.collector.ResType.Companion.DIMENS
import com.skin.skincore.collector.ResType.Companion.DRAWABLE
import com.skin.skincore.collector.ResType.Companion.MIPMAP
import com.skin.skincore.collector.ResType.Companion.UNDEFINE

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER
)
@StringDef(COLOR, DRAWABLE, MIPMAP, DIMENS, UNDEFINE)
annotation class ResType {
    companion object {
        const val COLOR = "color"
        const val DRAWABLE = "drawable"
        const val MIPMAP = "mipmap"
        const val DIMENS = "dimens"
        const val UNDEFINE = "undefine"
    }
}
