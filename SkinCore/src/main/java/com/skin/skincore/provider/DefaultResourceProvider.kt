package com.skin.skincore.provider

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * 默认资源提供器
 */
class DefaultResourceProvider(private val ctx: Context) : IResourceProvider {
    override fun getDefaultResourceProvider(): IResourceProvider {
        return this
    }
    override fun getColor(resId: Int, theme: Theme?): Int {
        return ctx.getColor(resId)
    }

    override fun getStateColor(resId: Int, theme: Theme?): ColorStateList {
        return ctx.getColorStateList(resId)
    }

    override fun getDrawable(resId: Int, theme: Theme?): Drawable {
        return ResourcesCompat.getDrawable(ctx.resources, resId, theme)!!
    }

    override fun getResourceEntryName(resId: Int): String {
        return ctx.resources.getResourceEntryName(resId)
    }

    override fun getCurrentResource(): Resources = ctx.resources
}
