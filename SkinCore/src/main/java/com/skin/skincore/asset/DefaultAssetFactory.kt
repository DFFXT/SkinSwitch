package com.skin.skincore.asset

import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 默认资源构造
 */
class DefaultAssetFactory : IAssetFactory {
    override fun createAsset(context: Context, skinPathProvider: ISkinPathProvider): IAsset {
        return Asset(context, skinPathProvider)
    }
}
