package com.skin.skincore.asset

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.skin.log.Logger
import com.skin.skincore.reflex.addAssetPathMethod
import java.io.File
import java.util.*
import kotlin.collections.HashMap

internal object AssetLoader {
    private var asset: Asset? = null
    private var map = HashMap<String, Asset?>()

    // 当前皮肤资源
    /* fun getCurrentThemeAsset(): Asset? {
         return asset
     }*/
    fun getAsset(context: Context, path: String?): Asset? {
        path ?: return null
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
        path ?: return null
        if (!File(path).exists()) {
            Logger.e("AssetLoader", "skin pack:$path not exists")
        }
        val pm = context.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        if (pkgInfo == null) {
            Logger.e("AssetLoader", "invalid skin pack")
        }
        val pkgName = pkgInfo?.packageName
        if (pkgName.isNullOrEmpty()) return null
        try {
            val manager1 = AssetManager::class.java.newInstance()
            addAssetPathMethod.invoke(manager1, path)

            val res = Resources(
                manager1,
                context.resources.displayMetrics,
                context.resources.configuration
            )
            asset = Asset(pkgName, res)
            return asset
        } catch (e: Exception) {
            Logger.d("AssetLoader", "create asset failed")
            e.printStackTrace()
        }
        return null
    }

    fun getAll(): HashMap<String, Asset?> = map
}
