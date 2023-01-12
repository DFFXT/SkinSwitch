package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.SkinManager
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
                provider = resourceProviderFactory.getResourceProvider(context, theme)
                    ?: defaultResourceProvider
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
                provider = resourceProviderFactory.getPathProvider(theme)
                pathMap[theme] = provider
            }
            provider
        }
    }
}
