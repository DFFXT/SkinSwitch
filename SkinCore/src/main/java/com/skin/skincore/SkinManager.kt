package com.skin.skincore

import android.app.Application
import android.content.Context
import android.view.View
import com.skin.skincore.asset.AssetLoader
import com.skin.skincore.collector.DefaultCollector
import com.skin.skincore.loader.ContextLoader
import com.skin.skincore.loader.ContextLoaderServer
import com.skin.skincore.plug.SkinPackDeveloping
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ResourceProviderFactory
import com.skin.skincore.provider.ResourcesProviderManager

/**
 * 皮肤管理器
 */
object SkinManager {
    const val DEFAULT_THEME = 0
    private val loaderServer = ContextLoaderServer()
    private val collectors = DefaultCollector()
    private lateinit var providerFactory: ResourceProviderFactory
    private lateinit var application: Application
    private var theme: Int = DEFAULT_THEME

    /**
     * 对当前context进行初始化，凡是通过该context进行inflate的对象均进行view拦截
     */
    fun init(ctx: Application, providerFactory: ResourceProviderFactory) {
        this.application = ctx
        this.providerFactory = providerFactory
        ResourcesProviderManager.init(ctx, providerFactory)
        SkinPackDeveloping.sinkPackInstall(ctx)
        makeContextSkinAble(ctx)
        ContextInterceptor(ctx)
    }

    /**
     * 使对应context支持换肤
     */
    fun makeContextSkinAble(context: Context) {
        if (!loaderServer.containsContext(context)) {
            val asset = AssetLoader.getAsset(
                application,
                ResourcesProviderManager.getPathProvider(theme)?.getSkinPath()
            )
            loaderServer.addLoader(
                ContextLoader(
                    context,
                    asset,
                    ResourcesProviderManager.getResourceProvider(context, theme),
                    collectors
                )
            )
        }
    }

    fun destroy(ctx: Context) {
        loaderServer.removeLoader(ctx)
    }

    /**
     * 新增支持的属性
     * @param id 举例：android.R.attr.textColor
     * @param name 举例：“textColor”
     */
    fun addAttributeCollection(id: Int, name: String) {
        collectors.addSupportAttr(id, name)
    }

    /**
     * 皮肤切换，将对应的context进行切换
     * @param ctx 如果为null，单独切换，如果不null全局切换
     * 强烈不推荐ctx不为null的情况
     */
    fun switchTheme(theme: Int, ctx: Context? = null) {
        if (ctx == null) {
            this.theme = theme
        }
        val asset = AssetLoader.getAsset(
            application,
            ResourcesProviderManager.getPathProvider(theme)?.getSkinPath()
        )
        loaderServer.switchTheme(
            asset,
            ResourcesProviderManager.getResourceProvider(application, theme), ctx
        )
    }

    /**
     * 强制刷新，比如白天黑夜变化时可以调用
     */
    fun forceRefreshView(context: Context?) {
        loaderServer.forceRefreshView()
    }

    /**
     * 获取当前context的资源提供器，如果context不是可换肤context，则返回默认资源提供器
     */
    fun getResourceProvider(context: Context): IResourceProvider {
        return loaderServer.getContextLoader(context)?.getResourceProvider() ?: ResourcesProviderManager.getResourceProvider(context, DEFAULT_THEME)
    }

    /**
     * 从换肤中移除
     */
    fun removeView(view: View) {
        loaderServer.getContextLoader(view.context)?.removeView(view)
    }
}
