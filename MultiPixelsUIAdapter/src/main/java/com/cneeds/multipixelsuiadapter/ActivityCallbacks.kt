package com.cneeds.multipixelsuiadapter

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

internal class ActivityCallbacks: ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        MultiPixelsAdjustManager.makeContextMultiPixelsAble(activity)
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
    }
}