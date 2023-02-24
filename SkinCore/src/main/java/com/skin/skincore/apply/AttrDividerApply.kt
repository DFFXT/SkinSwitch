package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.ListView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrDividerApply : BaseViewApply<ListView>(android.R.attr.divider, ListView::class.java) {
    override fun apply(
        view: ListView,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.divider = provider.getDrawable(resId, theme)
            }
        }
    }
}
