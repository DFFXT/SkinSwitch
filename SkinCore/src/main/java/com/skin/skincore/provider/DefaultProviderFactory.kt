package com.skin.skincore.provider

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.skin.log.Logger
import com.skin.skincore.asset.AssetLoader
import java.io.File

/**
 * 默认的资源构造提供者
 */
abstract class DefaultProviderFactory : ResourceProviderFactory {
    private lateinit var defProvider: IResourceProvider
    private val logTag = "DefaultProviderFactory"
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
        } else {
            Logger.d(logTag, "there is no resource provider for theme: $theme")
        }
        return defProvider
    }

    override fun getDefaultProvider(ctx: Context) = DefaultResourceProvider(ctx)
}
