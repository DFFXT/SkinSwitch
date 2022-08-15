package com.skin.skincore.apply

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import com.skin.skincore.collector.DefaultCollector

class TextViewApply : ViewApply<TextView>() {
    override fun apply(view: View) = view is TextView
    override fun applyColor(view: TextView, attrName: String, color: Int?) {
        when (attrName) {
            DefaultCollector.ATTR_TEXT_COLOR -> {
                color ?: return
                view.setTextColor(color)
            }
            else -> {
                super.applyColor(view, attrName, color)
            }
        }
    }

    override fun applyStateColor(view: TextView, attrName: String, color: ColorStateList?) {
        when (attrName) {
            DefaultCollector.ATTR_TEXT_COLOR -> {
                view.setTextColor(color)
            }
            else -> {
                super.applyStateColor(view, attrName, color)
            }
        }
    }
}
