package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.TextView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

class AttrTextColorApply : BaseViewApply<TextView>(android.R.attr.textColor, TextView::class.java) {
    override fun apply(view: TextView, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        when (resType) {
            ResType.DRAWABLE -> {
                view.setTextColor(provider.getStateColor(resId, theme))
            }
            ResType.COLOR -> {
                view.setTextColor(provider.getColor(resId, theme))
            }
        }
    }
}
