package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.TextView
import com.skin.log.Logger
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrTextColorApply : BaseViewApply<TextView>(android.R.attr.textColor, TextView::class.java) {
    override fun apply(view: TextView, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        Logger.i("test_textColor", view.text.toString() +" -->"+ resType)
        when (resType) {
            ResType.DRAWABLE -> {
                val color = provider.getStateColor(resId, theme)
                view.setTextColor(color)
                Logger.i("test_textColor", "" + view.currentTextColor  +";default:"+color.defaultColor)
            }
            ResType.COLOR -> {
                val color = provider.getStateColor(resId, theme)
                view.setTextColor(color)
                Logger.i("test_textColor", "" + view.currentTextColor  +";default:"+color.defaultColor)
            }
        }
    }
}
