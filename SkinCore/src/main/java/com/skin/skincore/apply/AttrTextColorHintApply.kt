package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.TextView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * TextView提示语颜色
 */
internal class AttrTextColorHintApply : BaseViewApply<TextView>(android.R.attr.textColorHint, TextView::class.java) {
    override fun apply(
        view: TextView,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.setHintTextColor(provider.getStateColor(resId, theme))
            }
        }
    }
}
