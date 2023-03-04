package com.skin.skincore.asset

import android.content.Context
import android.content.res.AssetManager
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源包加载接口
 */
interface IResourceLoader {
    fun createAsset(context: Context, provider: ISkinPathProvider): AssetInfo
}