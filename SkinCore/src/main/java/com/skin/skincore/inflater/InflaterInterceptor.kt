package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.DefaultApplyDispatcher
import com.skin.skincore.collector.getViewUnion
import com.skin.skincore.provider.DefaultResourceProvider
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.inflater

/**
 * inflater.factory替换
 */
internal object InflaterInterceptor {

    const val applyImmediately = true

    private var currentProvider: IResourceProvider? = null
    private var defaultResourceProvider: IResourceProvider? = null
    private val dispatcher by lazy {
        DefaultApplyDispatcher(currentProvider ?: defaultResourceProvider!!)
    }

    /**
     * 给context注入自己的factory
     */
    fun addInterceptor(context: Context) {
        if (context is ContextThemeWrapper) {
            if (defaultResourceProvider == null) {
                defaultResourceProvider = DefaultResourceProvider(context.applicationContext)
            }
            val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
            inflater.set(context, skinLayoutInflater)

            skinLayoutInflater.onViewCreatedListener = object : IOnViewCreated {
                override fun onViewCreated(view: View, name: String, attributeSet: AttributeSet) {
                    SkinManager.collectors.forEach {
                        val attrs = it.parser.parse(view, attributeSet)
                        SkinManager.viewContainer.add(view, attrs)
                        attrs.forEach { attr ->
                            dispatcher.apply(view, attr)
                        }
                    }
                }
            }
        }
    }

    fun switchTheme(provider: IResourceProvider) {
        this.currentProvider = provider
        dispatcher.switchProvider(provider)
        SkinManager.viewContainer.forEach { viewRef ->
            val v = viewRef.key
            v.getViewUnion()?.forEach {
                dispatcher.apply(v, it.value)
            }
        }
    }

    fun destroy(context: Context) {
        // dispatcher.remove(context.hashCode())
    }
}
