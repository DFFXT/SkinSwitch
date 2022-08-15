package com.skin.skincore.loader

import android.content.Context

class ContextLoaderServer : Iterable<ContextLoader> {
    private val loaderContainer = LinkedHashSet<ContextLoader>()

    val size: Int
        get() = loaderContainer.size

    /**
     * 将contextLoader添加到容器
     */
    fun addLoader(contextLoader: ContextLoader) {
        if (contextLoader.ctxRef.get() == null) {
            return
        }
        if (loaderContainer.find { it.ctxRef.get() == contextLoader.ctxRef.get() } == null) {
            loaderContainer.add(contextLoader)
        }
    }

    /**
     * 移除
     */
    fun removeLoader(context: Context) {
        loaderContainer.removeAll {
            it.ctxRef.get() == context
        }
    }

    override fun iterator(): Iterator<ContextLoader> {
        return loaderContainer.iterator()
    }
}
