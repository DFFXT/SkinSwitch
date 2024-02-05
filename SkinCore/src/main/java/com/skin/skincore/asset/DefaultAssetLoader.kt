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
import java.util.WeakHashMap

/**
 * 默认的皮肤包加载逻辑
 * 加载外部apk
 */
class DefaultAssetLoader : IResourceLoader {
    private val default by lazy { DefaultAssetProvider() }

    // AssetManager用来读写文件，即使是不同的Configuration，也能复用
    // key：AssetManager，value：String
    private val assetManagerMap = WeakHashMap<AssetInfo, String>()
    override fun createAsset(
        application: Context,
        configuration: Configuration,
        provider: ISkinPathProvider,
    ): AssetInfo {
        val path = provider.getSkinPath()
        // 查看缓存
        val assetInfo =  assetManagerMap.entries.find { it.value == path }?.key
        if (assetInfo != null) {
            return assetInfo
        }
        // 没有缓存
        val pair = createAssetManager(path, application)

        if (pair != null) {
            return AssetInfo(pair.second, pair.first)
        }
        // 没有创建成功
        return default.getDefault(application, provider)
    }

    /**
     * 根据path创建
     * @return key: pkgName
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
