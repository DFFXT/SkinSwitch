package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrForegroundTintApply : BaseViewApply<View>(android.R.attr.foregroundTint) {
    override fun apply(
        view: View,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.foregroundTintList = provider.getStateColor(resId, theme)
            }
        }
    }
}
