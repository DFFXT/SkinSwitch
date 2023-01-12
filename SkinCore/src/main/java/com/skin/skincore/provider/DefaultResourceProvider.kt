package com.skin.skincore.provider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * 默认资源提供器
 */
class DefaultResourceProvider(private val ctx: Context) : IResourceProvider {
    override fun getColor(resId: Int?): Int? {
        resId ?: return null
        return ctx.getColor(resId)
    }

    override fun getStateColor(resId: Int?): ColorStateList? {
        resId ?: return null
        return ctx.getColorStateList(resId)
    }

    override fun getDrawable(resId: Int?): Drawable? {
        resId ?: return null
        return ResourcesCompat.getDrawable(ctx.resources, resId, ctx.theme)
    }
}
