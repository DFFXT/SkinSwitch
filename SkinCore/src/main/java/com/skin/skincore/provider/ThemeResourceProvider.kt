package com.skin.skincore.provider

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * 加载外部皮肤
 */
class ThemeResourceProvider(
    private val ctx: Context,
    private val res: Resources,
    private val pkg: String
) : IResourceProvider {
    override fun getColor(resId: Int?): Int? {
        resId ?: return null
        val name = ctx.resources.getResourceName(resId)
        val id = res.getIdentifier(name, "color", pkg)
        return if (id != 0) {
            res.getColor(id)
        } else {
            ctx.getColor(resId)
        }
    }

    override fun getStateColor(resId: Int?): ColorStateList? {
        resId ?: return null
        val name = ctx.resources.getResourceName(resId)
        val id = res.getIdentifier(name, "color", pkg)
        return if (id != 0) {
            res.getColorStateList(id)
        } else {
            ctx.getColorStateList(resId)
        }
    }

    override fun getDrawable(resId: Int?): Drawable? {
        TODO("Not yet implemented")
    }
}
