package com.skin.skincore.asset

import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源生成器
 */
interface IAssetFactory {
    /**
     * 获取Asset对应的key用于缓存，相同的key不会重复调用[createAsset]
     * 这里默认实现，有需求可以自定义，比如通过不同的context来加载不同的资源包
     */
    fun getAssetKey(context: Context, skinPathProvider: ISkinPathProvider): AssetLoaderManager.AssetKey {
        return AssetLoaderManager.AssetKey(context, skinPathProvider.getSkinPath(), context.resources.displayMetrics)
    }
    fun createAsset(context: Context, skinPathProvider: ISkinPathProvider): IAsset
}
