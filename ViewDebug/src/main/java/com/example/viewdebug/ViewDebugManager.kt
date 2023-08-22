package com.example.viewdebug

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.viewdebug.listener.ActivityStackCallback
import com.example.viewdebug.ui.EmptyPage
import com.example.viewdebug.ui.UIControl
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.UiControlConfig
import com.example.viewdebug.ui.image.ViewDebugImageManager
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * 视图调试工具
 * 功能：
 * 1. 查看层级信息
 * 2. 查看图片信息
 */
object ViewDebugManager {
    private val activityLifecycleCallbacks = object : ActivityStackCallback() {
        private var init = false
        private val activities = LinkedList<Activity>()
        private var currentActivity: Activity? = null
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            super.onActivityCreated(p0, p1)
            currentActivity = p0
            activities.add(0, p0)
            uiControl.onActivityChange(WeakReference(p0))
            if (!init) {
                addPage(ViewDebugImageManager.getPage())
                addPage(EmptyPage())
                init = true
            }
        }

        override fun onActivityResumed(p0: Activity) {
            super.onActivityResumed(p0)
            currentActivity = p0
            uiControl.onActivityChange(WeakReference(p0))
            uiControl.show()
        }

        override fun onActivityDestroyed(p0: Activity) {
            super.onActivityDestroyed(p0)
            activities.remove(p0)
            // 所有页面退出后直接隐藏
            if (activities.isEmpty()) {
                uiControl.close()
            }
        }

        override fun onActivityStopped(p0: Activity) {
            super.onActivityStopped(p0)
            // 应用后台，隐藏
            if (p0 == currentActivity) {
                uiControl.close()
            }
        }
    }
    private lateinit var app: Application
    private val uiControl by lazy { UIControl(app) }
    fun init(app: Application) {
        if (this::app.isInitialized) return
        this.app = app
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun addPage(page: UIPage) {
        uiControl.loadPage(page)
    }

    fun removePage(page: UIPage) {
        uiControl.removePage(page)
    }

    /*fun destroy() {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        // app 持有不用销毁
        uiControl.destroy()
    }*/


    /**
     * 更新控制栏显示图标
     */
    fun updatePosition(uiControlConfig: UiControlConfig) {
        uiControl.updatePosition(uiControlConfig)
    }
}
