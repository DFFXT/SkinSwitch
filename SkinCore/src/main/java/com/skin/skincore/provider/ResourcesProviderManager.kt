package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.AssetLoader
import com.skin.skincore.collector.applyNight
import java.util.*

/**
 * 资源提供者管理
 */
object ResourcesProviderManager {
    private val map = WeakHashMap<Int, IResourceProvider>()
    private val pathMap = WeakHashMap<Int, ISkinPathProvider>()
    private lateinit var defaultResourceProvider: IResourceProvider
    private lateinit var resourceProviderFactory: ResourceProviderFactory
    fun init(context: Context, resourceProviderFactory: ResourceProviderFactory) {
        defaultResourceProvider = resourceProviderFactory.getDefaultProvider(context)
        this.resourceProviderFactory = resourceProviderFactory
    }

    /**
     * 根据主题获取资源提供者
     */
    fun getResourceProvider(context: Context, theme: Int): IResourceProvider {
        return if (theme == SkinManager.DEFAULT_THEME) {
            defaultResourceProvider
        } else {
            var provider = map[theme]
            if (provider == null) {
                val asset = AssetLoader.getAsset(context, getPathProvider(theme)?.getSkinPath())
                    ?: throw IllegalArgumentException("no path for theme: $theme")
                provider = resourceProviderFactory.getResourceProvider(
                    context,
                    theme,
                    asset,
                    defaultResourceProvider
                )
                map[theme] = provider
            }
            provider
        }
    }

    fun getPathProvider(theme: Int): ISkinPathProvider? {
        return if (theme == SkinManager.DEFAULT_THEME) {
            null
        } else {
            var provider = pathMap[theme]
            if (provider == null) {
                provider = resourceProviderFactory.getSkinPathProvider(theme)
                pathMap[theme] = provider
            }
            provider
        }
    }

    fun getSkinFolder(): String {
        return resourceProviderFactory.getSkinFolder()
    }

    /**
     * 切换为黑夜模式
     */
    fun applyNight(isNight: Boolean) {
        AssetLoader.getAll().values.forEach {
            it?.res?.applyNight(isNight)
        }
    }
}
