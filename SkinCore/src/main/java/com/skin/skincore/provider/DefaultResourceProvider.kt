package com.skin.skincore.provider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * 默认资源提供器
 */
class DefaultResourceProvider(private val ctx: Context) : IResourceProvider {
    override fun getDefaultResourceProvider(): IResourceProvider {
        return this
    }
    override fun getColor(resId: Int): Int {
        return ctx.getColor(resId)
    }

    override fun getStateColor(resId: Int): ColorStateList {
        return ctx.getColorStateList(resId)
    }

    override fun getDrawable(resId: Int): Drawable {
        return ResourcesCompat.getDrawable(ctx.resources, resId, ctx.theme)!!
    }

    override fun getResourceEntryName(resId: Int): String {
        return ctx.resources.getResourceEntryName(resId)
    }
}
