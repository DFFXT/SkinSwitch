package com.skin.skincore.provider

import android.content.Context

/**
 * 构造资源提供器
 */
interface ResourceProviderFactory {
    fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider
}
