package com.skin.skincore.asset

import android.app.Application
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 默认资源构造
 */
class DefaultAssetFactory : IAssetFactory {
    override fun createAsset(application: Application, skinPathProvider: ISkinPathProvider): IAsset {
        return Asset(application, skinPathProvider)
    }
}
