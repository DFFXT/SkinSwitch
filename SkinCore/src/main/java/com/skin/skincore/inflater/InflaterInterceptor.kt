package com.skin.skincore.inflater

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import com.skin.log.Logger
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
    @SuppressLint("BlockedPrivateApi")
    fun addInterceptor(context: Context) {
        if (context is ContextThemeWrapper) {
            if (defaultResourceProvider == null) {
                defaultResourceProvider = DefaultResourceProvider(context.applicationContext)
            }
            // /var skinLayoutInflater: SkinLayoutInflater? = null
            if (context is Activity) {
                val phoneWindowClass = Class.forName("com.android.internal.policy.PhoneWindow")
                // todo api 32 forbidden
                val field = phoneWindowClass.getDeclaredField("mLayoutInflater")
                field.isAccessible = true
                val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
                field.set(context.window, skinLayoutInflater)
                setInflateListener(skinLayoutInflater)
            }
            if (LayoutInflater.from(context) !is SkinLayoutInflater) {
                val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
                inflater.set(context, skinLayoutInflater)
                setInflateListener(skinLayoutInflater)
            }
        } else {
            Logger.d("InflaterInterceptor", "not support context: ${context::class.java.name}")
        }
    }

    private fun setInflateListener(inflater: SkinLayoutInflater) {
        inflater.onViewCreatedListener = object : IOnViewCreated {
            override fun onViewCreated(
                view: View,
                name: String,
                attributeSet: AttributeSet
            ) {
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

    fun switchTheme(provider: IResourceProvider) {
        // todo
        if (defaultResourceProvider == null) return
        this.currentProvider = provider
        dispatcher.switchProvider(defaultResourceProvider!!)
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
