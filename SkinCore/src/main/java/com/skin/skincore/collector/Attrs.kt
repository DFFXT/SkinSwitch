package com.skin.skincore.collector

import androidx.annotation.StringDef

class Attrs(val resId: Int?, val name: String) {
    companion object {
        const val COLOR = "color"
        const val Drawable = "drawable"
        const val STATE_COLOR = "stateColor"
        const val UNDEFINE = "undefine"
    }

    @set:ResType
    var resType: String = UNDEFINE

    @Target(AnnotationTarget.PROPERTY_SETTER)
    @StringDef(COLOR, Drawable, STATE_COLOR, UNDEFINE)
    annotation class ResType
}
