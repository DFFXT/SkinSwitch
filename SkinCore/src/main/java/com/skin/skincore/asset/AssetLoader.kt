package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.skin.log.Logger
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.reflex.addAssetPathMethod
import java.io.File

internal object AssetLoader {
    private var map = HashMap<String?, Asset?>()

    // 当前皮肤资源
    /* fun getCurrentThemeAsset(): Asset? {
         return asset
     }*/
    fun getAsset(context: Context, path: String?): Asset? {
        var asset = map[path]
        if (map.containsKey(path)) {
            return asset
        } else {
            asset = createResource(context, path)
            map[path] = asset
        }
        return asset
    }

    private fun createResource(context: Context, path: String?): Asset? {
        if (path == null || !File(path).exists()) {
            Logger.e("AssetLoader", "skin pack:$path not exists")
            return defaultAsset(context)
        }
        val pm = context.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        if (pkgInfo == null) {
            Logger.e("AssetLoader", "invalid skin pack")
        }
        val pkgName = pkgInfo?.packageName
        if (pkgName.isNullOrEmpty()) return defaultAsset(context)
        try {
            val manager1 = AssetManager::class.java.newInstance()
            addAssetPathMethod.invoke(manager1, path)

            val res = Resources(
                manager1,
                context.resources.displayMetrics,
                context.resources.configuration
            )
            return Asset(context.applicationContext as Application, pkgName, res)
        } catch (e: Exception) {
            Logger.d("AssetLoader", "create asset failed")
            e.printStackTrace()
        }
        return defaultAsset(context)
    }

    /**
     * 创建默认皮肤包
     */
    private fun defaultAsset(context: Context): Asset {
        val r = context.resources
        val config = if (r is MergeResource) {
            r.default.configuration
        } else r.configuration
        return Asset(context.applicationContext as Application, context.packageName, context.createConfigurationContext(config).resources)
    }

    fun getAll(): HashMap<String?, Asset?> = map
}
