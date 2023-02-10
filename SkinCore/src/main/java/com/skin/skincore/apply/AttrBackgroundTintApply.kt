package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.view.View
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.provider.IResourceProvider

class AttrBackgroundTintApply : BaseViewApply<View>(android.R.attr.backgroundTint) {
    override fun apply(view: View, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        //
        view.backgroundTintList = provider.getStateColor(resId, theme)
    }
}
