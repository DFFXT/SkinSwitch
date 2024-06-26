package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.IAsset

/**
 * 默认的资源构造提供者
 * 如果要提供外部皮肤包则需要重新[getSkinFolder]和[getSkinName]
 */
open class DefaultProviderFactory : ResourceProviderFactory {
    override fun getResourceProvider(ctx: Context, theme: Int, asset: IAsset, defaultProvider: IResourceProvider): IResourceProvider {
        return ThemeResourceProvider(asset, ctx.resources, defaultProvider)
    }

    override fun getSkinPathProvider(context: Context, theme: Int): ISkinPathProvider {
        return CustomSkinPathProvider(getSkinFolder(), getSkinName(context, theme), theme)
    }

    override fun getDefaultProvider(ctx: Context) = DefaultResourceProvider(ctx)

    override fun getSkinFolder(): String  = ""

    override fun getSkinName(context: Context, theme: Int): String  = ""
}
