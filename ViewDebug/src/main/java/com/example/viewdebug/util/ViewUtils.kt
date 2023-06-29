package com.example.viewdebug.util

import android.view.View
import android.view.ViewGroup

internal fun View.setSize(width: Int? = null, height: Int? = null) {
    val lp = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    lp.width = width ?: lp.width
    lp.height = height ?: lp.height
    layoutParams = lp
}
