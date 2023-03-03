package com.skin.skincore.asset

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.skin.log.Logger
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.reflex.addAssetPathMethod
import java.io.File

/**
 * 默认的皮肤包加载逻辑
 * 加载外部apk
 */
class DefaultResourceLoader:IResourceLoader {
    override fun createAsset(context: Context, provider: ISkinPathProvider): Asset {
        val path = provider.getSkinPath()
        if (!File(path).exists()) {
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
    private fun defaultAsset(context: Context): Asset {
        val r = context.resources
        val config = if (r is MergeResource) {
            r.default.configuration
        } else r.configuration
        return Asset(context.applicationContext as Application, context.packageName, context.createConfigurationContext(config).resources)
    }
}