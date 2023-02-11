package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.EditText
import android.widget.TextView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrTextColorHintApply : BaseViewApply<EditText>(android.R.attr.textColorHint, EditText::class.java) {
    override fun apply(
        view: EditText,
        resId: Int,
        resType: String,
        provider: IResourceProvider,
        theme: Theme?
    ) {
        when (resType) {
            ResType.DRAWABLE,
            ResType.COLOR -> {
                view.setHintTextColor(provider.getStateColor(resId, theme))
            }
        }
    }
}
