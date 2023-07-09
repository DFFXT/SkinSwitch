package com.skin.skincore.asset

import android.app.Application
import android.content.Context
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
        val pair = createAssetManager(path, application)
        if (pair != null) {
            return AssetInfo(pair.second, pair.first)
        }
        return default.getDefault(application, provider)
    }

    /**
     * 根据path创建
     */
    fun createAssetManager(path: String, ctx: Context): Pair<String, AssetManager>? {
        if (!File(path).exists()) {
            Logger.e("AssetLoader", "skin pack:$path not exists")
            return null
        }
        val pm = ctx.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        if (pkgInfo == null) {
            Logger.e("AssetLoader", "invalid skin pack")
        }
        val pkgName = pkgInfo?.packageName ?: return null
        try {
            val manager1 = AssetManager::class.java.newInstance()
            addAssetPathMethod.invoke(manager1, path)
            return Pair(pkgName, manager1)
        } catch (e: Exception) {
            Logger.d("AssetLoader", "create asset failed")
            e.printStackTrace()
        }
        return null
    }
}
