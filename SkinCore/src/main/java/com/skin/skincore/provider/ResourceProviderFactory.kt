package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.asset.IAsset

/**
 * 构造资源提供器
 * 使用时继承[DefaultProviderFactory]
 */
interface ResourceProviderFactory {
    /**
     * 返回资源提供器
     */
    fun getResourceProvider(ctx: Context, theme: Int, asset: IAsset, defaultProvider: IResourceProvider): IResourceProvider

    /**
     * 获取资源提供器的key，用于存储
     * 这里默认使用 包名+theme的方式，用包名参与的原因是有可能是插件化，加载的其它apk的view，包名参与可以支持插件化的换肤
     * 重写时需要注意[differentContextWithDifferentProvider]的返回值
     */
    fun getResourceProviderKey(ctx: Context, theme: Int): String {
        return ctx.packageName + "@" + theme + if (differentContextWithDifferentProvider()) ctx.hashCode() else ""
    }

    /**
     * 是否不同Context采用不同的Provider
     * 默认false，如果为true，会自动在context(activity)销毁时销毁provider
     * 如果为true，那么每个context对应的[getResourceProviderKey]方法返回值应该不一样
     */
    fun differentContextWithDifferentProvider() = false

    /**
     * 返回对应主题的资源路径
     */
    fun getSkinPathProvider(context: Context, theme: Int): ISkinPathProvider

    /**
     * 返回默认的资源，当皮肤包里面没有时适用默认的资源提供器
     */
    fun getDefaultProvider(ctx: Context): IResourceProvider

    /**
     * 皮肤包存储目录
     */
    fun getSkinFolder(): String

    fun getSkinName(context: Context, theme: Int): String
}
