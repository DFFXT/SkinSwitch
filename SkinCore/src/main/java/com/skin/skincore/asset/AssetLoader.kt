package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

internal object AssetLoader {
    private val resourceLoader: IResourceLoader = DefaultResourceLoader()
    private var map = HashMap<String?, Asset?>()

    // 当前皮肤资源
    /* fun getCurrentThemeAsset(): Asset? {
         return asset
     }*/
    fun getAsset(context: Context, provider: ISkinPathProvider): Asset? {
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

    private fun createResource(context: Context, provider: ISkinPathProvider): Asset {
        return Asset(context.applicationContext as Application, provider)
    }

    fun getAll(): HashMap<String?, Asset?> = map
}
