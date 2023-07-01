package com.skin.skincore.loader

import android.content.Context
import android.view.View
import com.skin.skincore.asset.IAsset
import com.skin.skincore.parser.AttrParseInterceptor
import com.skin.skincore.parser.AttrParseListener
import com.skin.skincore.provider.IResourceProvider
import java.util.LinkedList

internal class ContextLoaderServer {
    // 全局context容器
    private val loaderContainer = LinkedHashSet<ContextLoader>()

    // 视图解析拦截
    private var interceptor: AttrParseInterceptor? = null

    // 视图解析监听
    private val attrParseListeners = LinkedList<AttrParseListener>()

    /**
     * 将contextLoader添加到容器
     */
    fun addLoader(contextLoader: ContextLoader) {
        checkContext()
        if (contextLoader.isDestroyed()) {
            return
        }
        if (!loaderContainer.contains(contextLoader)) {
            contextLoader.interceptor = interceptor
            contextLoader.attrParseListeners = attrParseListeners
            loaderContainer.add(contextLoader)
        }
    }

    /**
     * @param asset 皮肤资源，如果为null则使用默认资源
     * @param iResourceProvider 资源提供器
     * @param ctx 要切换的context，如果为null则应用整体切换
     */
    fun switchTheme(
        asset: IAsset?,
        iResourceProvider: IResourceProvider,
        ctx: Context?,
        eventType: IntArray,
    ) {
        checkContext()
        if (ctx != null) {
            getContextLoader(ctx)?.switchTheme(asset, iResourceProvider, eventType)
        } else {
            loaderContainer.forEach {
                it.switchTheme(asset, iResourceProvider, eventType)
            }
        }
    }

    /**
     * 切换白天黑夜模式
     * 需要刷新才能生效
     */
    fun applyNight(isNight: Boolean, context: Context? = null) {
        if (context == null) {
            loaderContainer.forEach {
                it.applyNight(isNight)
            }
        } else {
            loaderContainer.find { it.equalContext(context) }?.applyNight(isNight)
        }
    }

    /**
     * 强制刷新view
     */
    fun forceRefreshView(context: Context? = null, eventType: IntArray) {
        if (context == null) {
            loaderContainer.forEach {
                it.refreshView(eventType)
            }
        } else {
            loaderContainer.find { it.equalContext(context) }?.refreshView(eventType)
        }
    }

    /**
     * 该context是否在换肤支持中
     */
    fun containsContext(context: Context): Boolean {
        return loaderContainer.find { it.equalContext(context) } != null
    }

    /**
     * 根据context获取loader
     */
    fun getContextLoader(context: Context): ContextLoader? {
        return loaderContainer.find { it.equalContext(context) }
    }

    /**
     * 移除
     */
    fun removeLoader(context: Context) {
        loaderContainer.removeAll {
            it.equalContext(context)
        }
    }

    fun removeView(view: View) {
        getContextLoader(view.context)?.removeView(view)
    }

    fun setAttrParseInterceptor(interceptor: AttrParseInterceptor) {
        this.interceptor = interceptor
        loaderContainer.forEach {
            it.interceptor = interceptor
        }
    }

    fun addAttrParseListener(attrParseListener: AttrParseListener) {
        attrParseListeners.add(attrParseListener)
    }

    /**
     * 确保一些非activity的contextLoader能够被移除
     */
    private fun checkContext() {
        val iterable = loaderContainer.iterator()
        while (iterable.hasNext()) {
            if (iterable.next().isDestroyed()) {
                iterable.remove()
            }
        }
    }
}
