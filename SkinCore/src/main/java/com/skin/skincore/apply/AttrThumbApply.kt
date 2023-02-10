package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.SeekBar
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrThumbApply : BaseViewApply<SeekBar>(android.R.attr.thumb) {
    override fun apply(
        view: SeekBar,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.thumb = provider.getDrawable(resId, theme)
            }
        }
    }
}
