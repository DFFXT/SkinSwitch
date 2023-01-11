package com.skin.skincore.provider

import android.content.Context

/**
 * 构造资源提供器
 */
interface ResourceProviderFactory {
    /**
     * 返回资源提供器
     */
    fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider?

    /**
     * 返回对应主题的资源路径
     */
    fun getPathProvider(theme: Int): ISkinPathProvider?

    /**
     * 返回默认的资源，当皮肤包里面没有时适用默认的资源提供器
     */
    fun getDefaultProvider(ctx: Context): IResourceProvider
}
