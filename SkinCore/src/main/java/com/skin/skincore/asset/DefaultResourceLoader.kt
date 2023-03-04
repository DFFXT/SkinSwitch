package com.skin.skincore.asset

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import com.skin.log.Logger
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.reflex.addAssetPathMethod
import java.io.File

/**
 * 默认的皮肤包加载逻辑
 * 加载外部apk
 */
class DefaultResourceLoader : IResourceLoader {
    override fun createAsset(context: Context, provider: ISkinPathProvider): AssetInfo {
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
            return AssetInfo(manager1, pkgName)
        } catch (e: Exception) {
            Logger.d("AssetLoader", "create asset failed")
            e.printStackTrace()
        }
        return defaultAsset(context)
    }

    private fun defaultAsset(context: Context): AssetInfo {
        // 这一步是为了创建当前应用的AssetManger
        val copyContext = context.createConfigurationContext(context.resources.configuration)
        return AssetInfo(copyContext.assets, context.packageName)
    }
}
