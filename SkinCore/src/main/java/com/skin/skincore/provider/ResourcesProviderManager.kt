package com.skin.skincore.provider

import android.content.Context
import com.skin.log.Logger
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.AssetLoaderManager
import java.util.WeakHashMap

/**
 * 资源提供者管理，如果某个theme下的资源需要释放，则需要调用[releaseProvider]进行释放，否则map会一直持有
 * 资源加载器[AssetLoaderManager]
 */
object ResourcesProviderManager {
    private val map = HashMap<String, IResourceProvider>()
    private val pathMap = WeakHashMap<Int, ISkinPathProvider>()
    private lateinit var resourceProviderFactory: ResourceProviderFactory

    // Resource对象创建器
    internal lateinit var resourceObjectCreator: ResourceObjectCreator
    private var resourceObjectCreatorHasSet = false

    // 默认资源路径提供器
    private val defaultSkinPathProvider = CustomSkinPathProvider("", "", SkinManager.DEFAULT_THEME)
    fun init(context: Context, resourceProviderFactory: ResourceProviderFactory) {
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
        val key = resourceProviderFactory.getResourceProviderKey(context, theme)
        var provider = map[key]
        if (provider == null) {
            Logger.d("ResourcesProviderManager", "create resources provider for context: ${context.packageName} ,theme: $theme")
            provider = if (theme == SkinManager.DEFAULT_THEME) {
                resourceProviderFactory.getDefaultProvider(context)
            } else {
                val asset = AssetLoaderManager.getAsset(context, getPathProvider(context, theme))
                    ?: throw IllegalArgumentException("no path for theme: $theme")
                resourceProviderFactory.getResourceProvider(
                    context,
                    theme,
                    asset,
                    getResourceProvider(context, SkinManager.DEFAULT_THEME),
                )
            }
            map[key] = provider
        }
        return provider
    }

    /**
     * 释放对应context和theme下的资源提供器
     * 一套皮肤资源只需调用该方法就能完全释放，其它地方都是弱引用
     */
    internal fun releaseProvider(context: Context, theme: Int) {
        val key = resourceProviderFactory.getResourceProviderKey(context, theme)
        val target = map.remove(key)
        Logger.d("ResourcesProviderManager", "releaseProvider: $target size:${map.size}")
    }

    fun getPathProvider(context: Context, theme: Int): ISkinPathProvider {
        return if (theme == SkinManager.DEFAULT_THEME) {
            defaultSkinPathProvider
        } else {
            var provider = pathMap[theme]
            if (provider == null) {
                provider = resourceProviderFactory.getSkinPathProvider(context, theme)
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
