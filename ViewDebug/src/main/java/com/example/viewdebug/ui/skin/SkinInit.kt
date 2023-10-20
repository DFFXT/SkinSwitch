package com.example.viewdebug.ui.skin

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.skin.skincore.asset.Asset
import com.skin.skincore.asset.AssetInfo
import com.skin.skincore.asset.AssetLoaderManager
import com.skin.skincore.asset.IAsset
import com.skin.skincore.asset.IAssetFactory
import com.skin.skincore.collector.applyNight
import com.skin.skincore.provider.ISkinPathProvider

class SkinInit {
    fun init() {
        AssetLoaderManager.setAssetFactory(object : IAssetFactory {
            override fun createAsset(context: Context, skinPathProvider: ISkinPathProvider): IAsset {
                return object : Asset(context, skinPathProvider) {
                    override fun createResource(assetInfo: AssetInfo, isNight: Boolean): Resources {
                        val config = Configuration(context.resources.configuration)
                        config.applyNight(isNight)
                        // 这里创建了Resource对象，会更改AssetManger内部的Configuration，影响其他Resource持有的AssetManger
                        // 所以需要一个Resource对应一个AssetManger
                        return ViewDebugResource(assetInfo.assetManager, context.resources.displayMetrics, config)
                    }
                }
            }
        })
    }
}