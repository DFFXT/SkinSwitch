package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.CompoundButton
import android.widget.ProgressBar
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

class AttrProgressDrawableApply : BaseViewApply<ProgressBar>(android.R.attr.progressDrawable, ProgressBar::class.java) {
    override fun apply(
        view: ProgressBar,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.MIPMAP,
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.progressDrawable = provider.getDrawable(resId, theme)
            }
        }
    }
}
