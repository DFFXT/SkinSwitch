package com.cneeds.multipixelsuiadapter

import android.view.View

class MultiPixelsLayoutWidthApply: BaseMultiPixelsApply<View>(android.R.attr.layout_width,  cls = View::class.java) {
    override fun apply(view: View, resId: Int): RunnableAfterInflate {
        val newId = resId.getResourceByScale(view.context)
        return object : RunnableAfterInflate {
            override fun run() {
                val lp = view.layoutParams
                lp.width = view.context.resources.getDimensionPixelSize(newId)
                view.layoutParams = lp
            }

        }
    }
}