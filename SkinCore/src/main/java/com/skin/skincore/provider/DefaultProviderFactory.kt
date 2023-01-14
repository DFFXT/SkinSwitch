package com.skin.skincore.provider

import android.content.Context

/**
 * 默认的资源构造提供者
 */
abstract class DefaultProviderFactory : ResourceProviderFactory {
    private lateinit var defProvider: IResourceProvider
    private val logTag = "DefaultProviderFactory"

    /**
     * 获取资源提供者，由于内部采用resource替换法，所以这里全部返回null
     */
    override fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider? {

        /*if (!this::defProvider.isInitialized) {
            defProvider = getDefaultProvider(ctx)
        }
        val path = getPathProvider(theme)?.getSkinPath() ?: ""
        if (path == TestResourceProvider::class.simpleName) {
            return TestResourceProvider()
        }
        val file = File(path)
        if (file.isFile && file.exists()) {
            val asset = AssetLoader.createResource(ctx, path)
            if (asset != null) {

                return ThemeResourceProvider(asset.res, asset.pkgName, ctx.resources, defProvider)
            }
        } else {
            Logger.d(logTag, "there is no resource provider for theme: $theme")
        }
        return defProvider*/
        return null
    }

    override fun getSkinPathProvider(theme: Int): ISkinPathProvider? {
        return CustomSkinPathProvider(getSkinFolder(), getSkinName(theme))
    }

    override fun getDefaultProvider(ctx: Context) = DefaultResourceProvider(ctx)
}
