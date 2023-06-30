package com.example.viewdebug.ui

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.viewdebug.databinding.LayoutViewDebugUiControlBinding
import java.lang.ref.WeakReference

/**
 * ui 控制
 */
class UIControl(private val ctx: Context) {
    private val rootView by lazy {
        LayoutViewDebugUiControlBinding.inflate(LayoutInflater.from(ctx))
    }
    private val pages = ArrayList<UIPage>()
    private val wm by lazy { ctx.getSystemService(WindowManager::class.java) }

    private var hostActivity: WeakReference<Activity>? = null

    // 是否显示
    var isShown = false
        private set

    fun show() {
        if (isShown) return
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or 0x00000040
        lp.format = PixelFormat.TRANSLUCENT
        lp.gravity = Gravity.END or Gravity.TOP
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.windowAnimations = -1
        WindowManager.LayoutParams::class.java.getDeclaredField("privateFlags").apply {
            isAccessible = true
            val flag = get(lp) as Int
            set(lp, flag or 0x00000040)
        }
        wm.addView(rootView.root, lp)
        isShown = true
    }

    fun close() {
        if (!isShown) return
        wm.removeViewImmediate(rootView.root)
        isShown = false
    }

    fun onActivityChange(hostActivity: WeakReference<Activity>) {
        this.hostActivity = hostActivity
        pages.forEach { it.onHostActivityChange(hostActivity) }
    }

    /**
     * 加载功能页
     */
    fun loadPage(page: UIPage) {
        val tabView = page.createTabView(ctx)
        tabView.setOnClickListener {
            switchPage(page)
        }
        hostActivity?.let {
            page.onHostActivityChange(it)
        }
        rootView.layoutControlBar.addView(tabView)
        pages.add(page)
    }

    /**
     * 切换为当前显示的page并移除其他page
     */
    private fun switchPage(delegate: UIPage) {
        if (!delegate.isOnShow) {
            rootView.layoutContent.addView(delegate.createContentView(ctx))
            delegate.onShow()
            pages.forEach {
                // 是否考虑多个共同显示
                if (it.isOnShow && it != delegate) {
                    rootView.layoutContent.removeView(it.createContentView(ctx))
                    it.onClose()
                }
            }
        }
    }

    fun removePage(p: UIPage) {
        rootView.layoutControlBar.removeView(p.createTabView(ctx))
        if (p.isOnShow) {
            rootView.layoutContent.removeView(p.createContentView(ctx))
            p.onClose()
        }
        p.onDestroy()
        pages.remove(p)
    }

    /**
     * 销毁
     */
    fun destroy() {
        pages.forEach {
            removePage(it)
        }
        close()
    }
}
