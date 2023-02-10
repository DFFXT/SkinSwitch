package com.skin.skincore

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * activity 监听，将activity添加到换肤框架
 */
class ActivitiesCallback private constructor() :
    Application.ActivityLifecycleCallbacks {

    companion object {
        fun register(application: Application) {
            application.registerActivityLifecycleCallbacks(ActivitiesCallback())
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        SkinManager.makeContextSkinAble(activity)
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        SkinManager.destroy(activity)
    }
}
