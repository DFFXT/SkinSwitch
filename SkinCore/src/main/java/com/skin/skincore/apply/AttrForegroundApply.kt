package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrForegroundApply : BaseViewApply<View>(android.R.attr.foreground) {
    override fun apply(
        view: View,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.MIPMAP,
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.foreground = provider.getDrawable(resId, theme)
            }
        }
    }
}
