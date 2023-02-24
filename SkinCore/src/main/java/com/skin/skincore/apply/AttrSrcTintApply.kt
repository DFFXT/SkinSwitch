package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.ImageView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.provider.IResourceProvider

open class AttrSrcTintApply : BaseViewApply<ImageView>(android.R.attr.tint, ImageView::class.java) {
    override fun apply(view: ImageView, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        view.imageTintList = provider.getStateColor(resId, theme)
    }
}
