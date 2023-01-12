package com.skin.skincore

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * activity 监听，将activity添加到换肤框架
 */
class ContextInterceptor(private val application: Application) :
    Application.ActivityLifecycleCallbacks {
    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        SkinManager.makeContextSkinAble(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        SkinManager.destroy(activity)
    }

    fun destroy() {
        application.unregisterActivityLifecycleCallbacks(this)
    }
}
