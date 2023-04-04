package com.skin.skincore.asset

import android.app.Application
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import com.skin.log.Logger
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.reflex.addAssetPathMethod
import java.io.File

/**
 * 默认的皮肤包加载逻辑
 * 加载外部apk
 */
class DefaultResourceLoader : IResourceLoader {
    private val default by lazy { DefaultResourceProvider() }
    override fun createAsset(
        application: Application,
        configuration: Configuration,
        provider: ISkinPathProvider,
    ): AssetInfo {
        val path = provider.getSkinPath()
        if (!File(path).exists()) {
            Logger.e("AssetLoader", "skin pack:$path not exists")
            return default.getDefault(application, provider)
        }
        val pm = application.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        if (pkgInfo == null) {
            Logger.e("AssetLoader", "invalid skin pack")
        }
        val pkgName = pkgInfo?.packageName
        if (pkgName.isNullOrEmpty()) return default.getDefault(application, provider)
        try {
            val manager1 = AssetManager::class.java.newInstance()
            addAssetPathMethod.invoke(manager1, path)
            return AssetInfo(manager1, pkgName)
        } catch (e: Exception) {
            Logger.d("AssetLoader", "create asset failed")
            e.printStackTrace()
        }
        return default.getDefault(application, provider)
    }
}
