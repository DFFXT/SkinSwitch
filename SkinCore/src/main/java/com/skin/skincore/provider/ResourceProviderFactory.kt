package com.skin.skincore.provider

import android.content.Context

/**
 * 构造资源提供器
 * 使用时继承[DefaultProviderFactory]
 */
interface ResourceProviderFactory {
    /**
     * 返回资源提供器
     */
    fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider?

    /**
     * 返回对应主题的资源名称
     */
    fun getSkinPathProvider(theme: Int): ISkinPathProvider?

    /**
     * 返回默认的资源，当皮肤包里面没有时适用默认的资源提供器
     */
    fun getDefaultProvider(ctx: Context): IResourceProvider

    /**
     * 皮肤包存储目录
     */
    fun getSkinFolder(): String

    fun getSkinName(theme: Int): String
}
