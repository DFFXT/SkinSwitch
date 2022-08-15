package com.skin.skincore.provider

import android.content.Context

class DefaultProviderFactory : ResourceProviderFactory {
    override fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider {
        return DefaultResourceProvider(ctx)
    }
}
