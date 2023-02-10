package com.skin.skincore.apply

import android.content.res.Resources.Theme
import android.widget.TextView
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.provider.IResourceProvider

internal class AttrDrawableEndApply : BaseViewApply<TextView>(android.R.attr.drawableTop, TextView::class.java) {
    override fun apply(view: TextView, resId: Int, resType: String, provider: IResourceProvider, theme: Theme?) {
        val drawable = provider.getDrawable(resId, theme)
        val drawables = view.compoundDrawablesRelative
        val currentDrawable = drawables[2]
        if (drawable.intrinsicWidth != 0) {
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        } else {
            drawable.setBounds(0, 0, currentDrawable.intrinsicWidth, currentDrawable.intrinsicHeight)
        }
        drawables[2] = drawable
        view.setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}
