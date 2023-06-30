package com.example.viewdebug.ui.skin

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.addViewSkinAttrs
import com.skin.skincore.collector.removeSkinAttr

object Skinable {
    internal var onChangeListener: ((View, resId: Int, attrId: Int) -> Unit)? = { v, resId, attrId ->
        // 使用SkinManager方式换肤
        if (resId != 0) {
            v.addViewSkinAttrs(
                Attrs(
                    resId,
                    attrId,
                ),
            )
        } else {
            v.removeSkinAttr(attrId)
        }

    }

    fun setModuleResourceSwitchListener(onChangeListener: ((View, resId: Int, attrId: Int) -> Unit)) {
        this.onChangeListener = onChangeListener
    }
}

internal fun ImageView.imageResource(resourceId: Int) {
    if (resourceId != 0) {
        setImageResource(resourceId)
    } else {
        setImageDrawable(null)
    }
    Skinable.onChangeListener?.invoke(this, resourceId, android.R.attr.src)
}

internal fun View.backgroundResource(backgroundId: Int) {
    if (backgroundId != 0) {
        setBackgroundResource(backgroundId)
    } else {
        background = null
    }
    Skinable.onChangeListener?.invoke(this, backgroundId, android.R.attr.background)
}

internal fun TextView.textColor(colorId: Int) {
    Skinable.onChangeListener?.invoke(this, colorId, android.R.attr.textColor)
}
