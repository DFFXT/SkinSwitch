package com.cneeds.multipixelsuiadapter

import android.view.View

class MultiPixelsLayoutHeightApply: BaseMultiPixelsApply<View>(android.R.attr.layout_height,  cls = View::class.java) {
    override fun apply(view: View, resId: Int): RunnableAfterInflate {
        val newId = resId.getResourceByScale(view.context)
        return object : RunnableAfterInflate {
            override fun run() {
                val lp = view.layoutParams
                lp.height = view.context.resources.getDimensionPixelSize(newId)
                view.layoutParams = lp
            }

        }
    }
}