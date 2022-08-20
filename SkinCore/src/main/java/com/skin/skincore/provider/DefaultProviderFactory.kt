package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.asset.AssetLoader
import java.io.File

/**
 * 默认的资源构造提供者
 */
abstract class DefaultProviderFactory : ResourceProviderFactory {
    private lateinit var defProvider: IResourceProvider
    override fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider {

        if (!this::defProvider.isInitialized) {
            defProvider = getDefaultProvider(ctx)
        }
        val path = getPathProvider(theme)?.getSkinPath() ?: ""
        if (path == TestResourceProvider::class.simpleName) {
            return TestResourceProvider()
        }
        // todo 文件访问
        val file = File(path)
        if (file.isFile && file.exists()) {
            val asset = AssetLoader.createResource(ctx, path)
            if (asset != null) {
                return ThemeResourceProvider(asset.res, asset.pkgName, ctx.resources, defProvider)
            }
        }
        return defProvider
    }

    override fun getDefaultProvider(ctx: Context) = DefaultResourceProvider(ctx)
}
