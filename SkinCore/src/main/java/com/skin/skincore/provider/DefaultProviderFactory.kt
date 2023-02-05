package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.asset.Asset

/**
 * 默认的资源构造提供者
 */
abstract class DefaultProviderFactory : ResourceProviderFactory {
    override fun getResourceProvider(ctx: Context, theme: Int, asset: Asset, defaultProvider: IResourceProvider): IResourceProvider {
        return ThemeResourceProvider(asset.res, asset.pkgName, ctx.resources, defaultProvider)
    }

    override fun getSkinPathProvider(theme: Int): ISkinPathProvider {
        return CustomSkinPathProvider(getSkinFolder(), getSkinName(theme))
    }

    override fun getDefaultProvider(ctx: Context) = DefaultResourceProvider(ctx)
}
