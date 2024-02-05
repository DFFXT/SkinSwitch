package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

class DefaultAssetProvider {
    fun getDefault(application: Context, provider: ISkinPathProvider): AssetInfo {
        // 这一步是为了创建当前应用的AssetManger
        val copyContext =
            application.createConfigurationContext(application.resources.configuration)
        return AssetInfo(copyContext.assets, application.packageName)
    }
}