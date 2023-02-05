package com.skin.skincore.apply

import android.view.View
import android.widget.TextView
import com.skin.skincore.collector.DefaultAttrCollector
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

class TextViewApply : ViewApply<TextView>() {
    override fun apply(view: View) = view is TextView
    private fun applyColor(
        view: TextView,
        resType: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (resType) {
            ResType.COLOR -> {
                view.setTextColor(provider.getColor(resId))
            }
            ResType.DRAWABLE -> {
                view.setTextColor(provider.getStateColor(resId))
            }
        }
    }

    override fun customApply(
        view: TextView,
        resType: String,
        attrName: String,
        provider: IResourceProvider,
        resId: Int
    ) {
        when (attrName) {
            DefaultAttrCollector.ATTR_TEXT_COLOR -> {
                applyColor(view, resType, provider, resId)
            }
            else -> {
                super.customApply(view, resType, attrName, provider, resId)
            }
        }
    }
}
