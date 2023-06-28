package com.example.viewdebug.ui.skin

import android.view.View
import android.widget.ImageView
import android.widget.TextView

object Skinable {
    internal var onChangeListener: ((View, resId: Int, attrId: Int) -> Unit)? = null
    fun setModuleResourceSwitchListener(onChangeListener: ((View, resId: Int, attrId: Int) -> Unit)) {
        this.onChangeListener = onChangeListener
    }
}

internal fun ImageView.imageResource(resourceId: Int) {
    setImageResource(resourceId)
    Skinable.onChangeListener?.invoke(this, resourceId, android.R.attr.src)
}

internal fun TextView.textColor(colorId: Int) {
    Skinable.onChangeListener?.invoke(this, colorId, android.R.attr.textColor)
}
