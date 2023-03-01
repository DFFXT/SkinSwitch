package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.CompoundButton
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrButtonApply : BaseViewApply<CompoundButton>(android.R.attr.button, CompoundButton::class.java) {
    override fun apply(
        view: CompoundButton,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.MIPMAP,
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.buttonDrawable = provider.getDrawable(resId, theme)
            }
        }
    }
}
