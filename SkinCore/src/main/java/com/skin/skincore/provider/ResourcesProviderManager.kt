package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.AssetLoaderManager
import java.util.WeakHashMap

/**
 * 资源提供者管理
 */
object ResourcesProviderManager {
    private val map = WeakHashMap<Int, IResourceProvider>()
    private val pathMap = WeakHashMap<Int, ISkinPathProvider>()
    private lateinit var defaultResourceProvider: IResourceProvider
    private lateinit var resourceProviderFactory: ResourceProviderFactory

    // Resource对象创建器
    internal lateinit var resourceObjectCreator: ResourceObjectCreator
    private var resourceObjectCreatorHasSet = false

    // 默认资源路径提供器
    private val defaultSkinPathProvider = CustomSkinPathProvider("", "", SkinManager.DEFAULT_THEME)
    fun init(context: Context, resourceProviderFactory: ResourceProviderFactory) {
        defaultResourceProvider = resourceProviderFactory.getDefaultProvider(context)
        if (!resourceObjectCreatorHasSet) {
            this.resourceObjectCreator = DefaultMergeResourceCreator
        }
        this.resourceProviderFactory = resourceProviderFactory
    }

    /**
     * 替换Resource对象创建器
     * 只允许设置一次
     * 要设置需要在init之前设置
     */
    fun replaceResourceObjectCreator(creator: ResourceObjectCreator) {
        if (resourceObjectCreatorHasSet) {
            throw IllegalStateException("resourceObjectCreator already replaced")
        }
        resourceObjectCreator = creator
        resourceObjectCreatorHasSet = true
    }

    /**
     * 根据主题获取资源提供者
     */
    fun getResourceProvider(context: Context, theme: Int): IResourceProvider {
        return if (theme == SkinManager.DEFAULT_THEME) {
            if (this::defaultResourceProvider.isInitialized) {
                defaultResourceProvider
            } else {
                DefaultResourceProvider(context)
            }
        } else {
            var provider = map[theme]
            if (provider == null) {
                val asset = AssetLoaderManager.getAsset(context, getPathProvider(theme))
                    ?: throw IllegalArgumentException("no path for theme: $theme")
                provider = resourceProviderFactory.getResourceProvider(
                    context,
                    theme,
                    asset,
                    defaultResourceProvider,
                )
                map[theme] = provider
            }
            provider
        }
    }

    fun getPathProvider(theme: Int): ISkinPathProvider {
        return if (theme == SkinManager.DEFAULT_THEME) {
            defaultSkinPathProvider
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
        AssetLoaderManager.getAll().keys.forEach {
            it?.applyNight(isNight)
        }
    }
}
