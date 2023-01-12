package com.skin.skincore.inflater

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import com.skin.log.Logger
import com.skin.skincore.reflex.inflater

/**
 * inflater.factory替换
 */
internal object InflaterInterceptor {

    /**
     * 给context注入自己的factory
     */
    @SuppressLint("BlockedPrivateApi")
    fun addInterceptor(context: Context, iOnViewCreated: IOnViewCreated) {
        if (context is ContextThemeWrapper) {
            // /var skinLayoutInflater: SkinLayoutInflater? = null
            if (context is Activity) {
                val phoneWindowClass = Class.forName("com.android.internal.policy.PhoneWindow")
                // todo api 32 forbidden
                val field = phoneWindowClass.getDeclaredField("mLayoutInflater")
                field.isAccessible = true
                val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
                field.set(context.window, skinLayoutInflater)
                skinLayoutInflater.onViewCreatedListener = iOnViewCreated
            }
            if (LayoutInflater.from(context) !is SkinLayoutInflater) {
                val skinLayoutInflater = SkinLayoutInflater(LayoutInflater.from(context), context)
                inflater.set(context, skinLayoutInflater)
                skinLayoutInflater.onViewCreatedListener = iOnViewCreated
            }
        } else {
            Logger.d("InflaterInterceptor", "not support context: ${context::class.java.name}")
        }
    }



    fun destroy(context: Context) {
        // dispatcher.remove(context.hashCode())
    }
}
