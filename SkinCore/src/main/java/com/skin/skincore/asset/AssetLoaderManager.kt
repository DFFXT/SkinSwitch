package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源加载管理，提供缓存
 */
object AssetLoaderManager {
    private var assetFactory: IAssetFactory = DefaultAssetFactory()
    private var map = HashMap<String?, IAsset?>()

    fun getAsset(context: Context, provider: ISkinPathProvider): IAsset? {
        val path = provider.getSkinPath()
        var asset = map[path]
        if (map.containsKey(path)) {
            return asset
        } else {
            asset = createResource(context, provider)
            map[path] = asset
        }
        return asset
    }

    private fun createResource(context: Context, provider: ISkinPathProvider): IAsset {
        return assetFactory.createAsset(context.applicationContext as Application, provider)
    }

    fun getAll(): HashMap<String?, IAsset?> = map

    /**
     * 设置资源加载器
     */
    fun setAssetFactory(factory: IAssetFactory) {
        this.assetFactory = factory
    }
}
