package com.skin.skincore

import android.content.Context
import android.view.View
import com.skin.skincore.collector.DefaultCollector
import com.skin.skincore.collector.IAttrCollector
import com.skin.skincore.collector.ViewContainer
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.loader.ContextLoader
import com.skin.skincore.loader.ContextLoaderServer
import com.skin.skincore.provider.DefaultResourceProvider
import com.skin.skincore.provider.ResourceProviderFactory

/**
 * 皮肤管理器
 */
object SkinManager {
    private val loaderServer = ContextLoaderServer()
    val viewContainer = ViewContainer()
    val collectors: MutableList<IAttrCollector<*>> = mutableListOf(DefaultCollector())
    private lateinit var providerFactory: ResourceProviderFactory
    private lateinit var application: Context
    private val defaultProvider by lazy {
        DefaultResourceProvider(application)
    }

    /**
     * 对当前context进行初始化，凡是通过该context进行inflate的对象均进行view拦截
     */
    fun init(ctx: Context, providerFactory: ResourceProviderFactory) {
        this.application = ctx.applicationContext
        this.providerFactory = providerFactory
        loaderServer.addLoader(ContextLoader(ctx))
        // AssetLoader().createContext(ctx, ctx.externalCacheDir!!.absolutePath +"/app-debug.apk")
    }

    fun destroy(ctx: Context) {
        loaderServer.removeLoader(ctx)
    }

    fun registerCollector(collector: IAttrCollector<View>) {
        collectors.add(collector)
    }

    fun switchTheme(theme: Int, ctx: Context? = null) {
        if (ctx == null) {
            loaderServer.forEach {
                val context = it.ctxRef.get()
                if (context != null) {
                    InflaterInterceptor.switchTheme(
                        providerFactory.getResourceProvider(
                            context,
                            theme
                        ) ?: defaultProvider
                    )
                }
            }
        } else {
            InflaterInterceptor.switchTheme(
                providerFactory.getResourceProvider(ctx, theme) ?: defaultProvider
            )
        }
    }
}
