package com.skin.skincore.collector

class Attrs(val resId: Int?, val name: String) {
    companion object {
        const val COLOR = "color"
        const val Drawable = "drawable"
        const val STATE_COLOR = "stateColor"
        const val UNDEFINE = "undefine"
    }
    var resType: String = UNDEFINE
}