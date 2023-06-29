package com.example.viewdebug

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.viewdebug.listener.ActivityStackCallback
import com.example.viewdebug.ui.EmptyPage
import com.example.viewdebug.ui.UIControl
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.ViewDebugImageManager
import java.lang.ref.WeakReference

/**
 * 视图调试工具
 * 功能：
 * 1. 查看层级信息
 * 2. 查看图片信息
 */
object ViewDebugManager {
    private val activityLifecycleCallbacks = object : ActivityStackCallback() {
        private var init = false
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            super.onActivityCreated(p0, p1)
            uiControl.onActivityChange(WeakReference(p0))
            if (!init) {
                addPage(ViewDebugImageManager.getPage())
                addPage(EmptyPage())
                uiControl.show()
                init = true
            }
        }
    }
    private lateinit var app: Application
    private val uiControl by lazy { UIControl(app) }
    fun init(app: Application) {
        this.app = app
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun addPage(page: UIPage) {
        uiControl.loadPage(page)
    }

    fun removePage(page: UIPage) {
        uiControl.removePage(page)
    }
}
