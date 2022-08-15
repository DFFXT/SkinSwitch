package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.DefaultApplyDispatcher
import com.skin.skincore.provider.DefaultResourceProvider
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.inflater

/**
 * inflater.factory替换
 */
internal object InflaterInterceptor {

    const val applyImmediately = true

    private val dispatchers = HashMap<Int, DefaultApplyDispatcher>()

    /**
     * 给context注入自己的factory
     */
    fun addInterceptor(context: Context) {
        if (context is ContextThemeWrapper) {
            val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
            inflater.set(context, skinLayoutInflater)
            val dispatcher = DefaultApplyDispatcher(DefaultResourceProvider(context))
            dispatchers[context.hashCode()] = dispatcher
            skinLayoutInflater.onViewCreatedListener = object : IOnViewCreated {
                override fun onViewCreated(view: View, name: String, attributeSet: AttributeSet) {
                    SkinManager.collectors.forEach {
                        val attrs = it.parser.parse(view, attributeSet)
                        SkinManager.viewContainer.add(view, *attrs.toTypedArray())
                        attrs.forEach { attr ->
                            dispatcher.apply(view, attr)
                        }
                    }
                }
            }
        }
    }

    fun switchTheme(provider: IResourceProvider) {
        dispatchers.forEach {
            it.value.switchProvider(provider)
        }
    }

    fun destroy(context: Context) {
        dispatchers.remove(context.hashCode())
    }
}
