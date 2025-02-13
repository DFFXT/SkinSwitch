package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源包加载接口
 */
interface IResourceLoader {
    /**
     * 如果返回null，则使用系统默认
     */
    fun createAsset(
        application: Context,
        configuration: Configuration,
        provider: ISkinPathProvider
    ): AssetInfo
}
