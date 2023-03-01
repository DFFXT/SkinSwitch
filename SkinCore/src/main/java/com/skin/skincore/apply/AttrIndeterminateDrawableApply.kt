package com.skin.skincore.apply

import android.content.res.Resources
import android.widget.ProgressBar
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

/**
 * ProgressBar android.R.attr.indeterminateDrawable 切换
 */
class AttrIndeterminateDrawableApply : BaseViewApply<ProgressBar>(android.R.attr.indeterminateDrawable, ProgressBar::class.java) {
    override fun apply(view: ProgressBar, resId: Int, resType: String, provider: IResourceProvider, theme: Resources.Theme?) {
        when (resType) {
            ResType.MIPMAP,
            ResType.DRAWABLE -> {
                val drawable = provider.getDrawable(resId, theme)
                // 这里不支持更改图片bounds
                view.indeterminateDrawable = drawable
            }
        }
    }
}
