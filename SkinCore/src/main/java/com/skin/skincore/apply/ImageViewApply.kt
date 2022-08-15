package com.skin.skincore.apply

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.skin.skincore.collector.DefaultCollector

class ImageViewApply : ViewApply<ImageView>() {
    override fun apply(view: View) = view is ImageView
    override fun applyDrawable(view: ImageView, attrName: String, drawable: Drawable?) {
        when (attrName) {
            DefaultCollector.ATTR_SRC -> {
                view.setImageDrawable(drawable)
            }
            else -> super.applyDrawable(view, attrName, drawable)
        }
    }
}
