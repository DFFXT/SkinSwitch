package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrBackgroundApply : BaseViewApply<View>(android.R.attr.background, View::class.java) {
    override fun apply(view: View, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        when (resType) {
            ResType.DRAWABLE -> {
                view.background = provider.getDrawable(resId, theme)
            }
            ResType.COLOR -> {
                view.setBackgroundColor(provider.getColor(resId, theme))
            }
        }
    }
}
