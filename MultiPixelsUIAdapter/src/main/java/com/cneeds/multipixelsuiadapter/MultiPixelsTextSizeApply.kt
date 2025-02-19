package com.cneeds.multipixelsuiadapter

import android.util.TypedValue
import android.widget.TextView

class MultiPixelsTextSizeApply: BaseMultiPixelsApply<TextView>(android.R.attr.textSize,  cls = TextView::class.java) {
    override fun apply(view: TextView, resId: Int): RunnableAfterInflate? {
        val newId = resId.getResourceByScale(view.context)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.context.resources.getDimension(newId))
        return null
    }
}