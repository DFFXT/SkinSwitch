package com.skin.skincore.inflater

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import com.skin.log.Logger
import com.skin.skincore.reflex.androidXContentThemeWrapperInflater
import com.skin.skincore.reflex.inflater

/**
 * inflater.factory替换
 */
internal object InflaterInterceptor {


    val plan1 = true

    /**
     * 给context注入自己的LayoutInflater
     * 这里有两种方式达成监听View的创建
     * 1. 代理LayoutInflater+代理Factory
     * 2. 代理Factory
     * 方案1可以监听Fragment中FragmentContainerView的创建（API>=21）
     * 方案2无法监听Fragment中FragmentContainerView的创建
     * 原因见[Fragment.getLayoutInflater]
     * 在[Fragment.getLayoutInflater]方法设置了一个Factory2，导致代理Factory优先级降低
     * 由于方案1是代理了LayoutInflater，重写了setFactory2方法，不会导致代理Factory优先级降低，所以方案1能监听FragmentContainerView
     *
     * 除非方案二中重写[Fragment.onGetLayoutInflater]方法，将需要设置的Factory2设置到代理Factory中
     *
     *
     * 然而，实际开发中对FragmentContainerView换肤是可以避免的，则方案2也可以
     */
    @SuppressLint("BlockedPrivateApi")
    fun addInterceptor(context: Context, iOnViewCreated: IOnViewCreated) {
        val origin = LayoutInflater.from(context)
        if (!plan1) {
            // 方案2
            LayoutInflaterDelegate.delegate(origin, origin, iOnViewCreated)
            return
        }
      /*  // 系统生成初始inflater的方式
        val inflater1 = PhoneLayoutInflater(ctx.getOuterContext())
        // 开发中使用获取inflater的方式
        val inflater2 = LayoutInflater.from(context)
        // inflater的拷贝
        val inflater3 = LayoutInflater.from(context).cloneInContext(context)*/

        // 方案1
        if (context is androidx.appcompat.view.ContextThemeWrapper) {
            val layoutInflaterDelegate =
                LayoutInflaterDelegate(origin, context)
            androidXContentThemeWrapperInflater.set(context, layoutInflaterDelegate)
            layoutInflaterDelegate.onViewCreatedListener = iOnViewCreated
        }

        if (context is ContextThemeWrapper) {
            // /var skinLayoutInflater: SkinLayoutInflater? = null
            if (context is Activity) {
                // 替换WindowPhone中inflater，因为这个inflater会被fragment复制
                val phoneWindowClass = Class.forName("com.android.internal.policy.PhoneWindow")
                // todo api 32 forbidden
                val field = phoneWindowClass.getDeclaredField("mLayoutInflater")
                field.isAccessible = true
                val layoutInflaterDelegate =
                    LayoutInflaterDelegate(origin, context)
                field.set(context.window, layoutInflaterDelegate)
                layoutInflaterDelegate.onViewCreatedListener = iOnViewCreated
            }
            // 替换ContextThemeWrapper中的局部变量
            if (LayoutInflater.from(context) !is LayoutInflaterDelegate) {
                val layoutInflaterDelegate =
                    LayoutInflaterDelegate(origin, context)
                inflater.set(context, layoutInflaterDelegate)
                layoutInflaterDelegate.onViewCreatedListener = iOnViewCreated
            }
        } else if (context is Application) {
            // application中的inflater，暂时代理Factory（看后面是否反射SystemServiceRegister里面的layout_inflater）
            LayoutInflaterDelegate.delegate(origin, origin, iOnViewCreated)
            Logger.d("InflaterInterceptor", "not support context: ${context::class.java.name}")
        }
    }
}
