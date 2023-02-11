package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.ImageView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.provider.IResourceProvider

open class AttrSrcApply : BaseViewApply<ImageView>(android.R.attr.src, ImageView::class.java) {
    override fun apply(view: ImageView, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        view.setImageDrawable(provider.getDrawable(resId, theme))
    }
}
