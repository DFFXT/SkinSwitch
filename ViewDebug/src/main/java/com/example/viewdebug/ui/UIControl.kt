package com.example.viewdebug.ui

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.viewdebug.databinding.LayoutViewDebugUiControlBinding
import com.example.viewdebug.databinding.ViewDebugLayoutMainContentBinding
import java.lang.ref.WeakReference

/**
 * ui 控制
 */
class UIControl(private val ctx: Context) {
    // 顶部控制区域
    private val uiControlBinding by lazy {
        LayoutViewDebugUiControlBinding.inflate(LayoutInflater.from(ctx))
    }

    // 内容区域
    private val contentBinding by lazy {
        ViewDebugLayoutMainContentBinding.inflate(LayoutInflater.from(ctx))
    }
    private val pages = ArrayList<UIPage>()
    private val wm by lazy { ctx.getSystemService(WindowManager::class.java) }

    private var hostActivity: WeakReference<Activity>? = null

    // 是否显示
    var isShown = false
        private set

    fun show() {
        if (isShown) return
        // 添加内容区域
        val contentLp = getLayoutParams()
        contentLp.width = ctx.resources.displayMetrics.widthPixels
        contentLp.height = ctx.resources.displayMetrics.heightPixels
        wm.addView(contentBinding.root, contentLp)
        // 添加控制栏
        val lp = getLayoutParams()
        wm.addView(uiControlBinding.root, lp)
        isShown = true
    }

    fun close() {
        if (!isShown) return
        wm.removeViewImmediate(uiControlBinding.root)
        wm.removeViewImmediate(contentBinding.root)
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
        uiControlBinding.layoutControlBar.addView(tabView)
        pages.add(page)
    }

    /**
     * 切换为当前显示的page并移除其他page
     */
    private fun switchPage(delegate: UIPage) {
        if (!delegate.isOnShow) {
            contentBinding.layoutContent.addView(delegate.createContentView(ctx))
            delegate.onShow()
            pages.forEach {
                // 是否考虑多个共同显示
                if (it.isOnShow && it != delegate) {
                    contentBinding.layoutContent.removeView(it.createContentView(ctx))
                    it.onClose()
                }
            }
            val lp = contentBinding.root.layoutParams as WindowManager.LayoutParams
            if (!delegate.enableTouch()) {
                lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            } else {
                lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            }
            wm.updateViewLayout(contentBinding.root, lp)
        }
    }

    fun removePage(p: UIPage) {
        uiControlBinding.layoutControlBar.removeView(p.createTabView(ctx))
        if (p.isOnShow) {
            contentBinding.layoutContent.removeView(p.createContentView(ctx))
            p.onClose()
        }
        p.onDestroy()
        pages.remove(p)
    }

    private fun getLayoutParams(): WindowManager.LayoutParams {
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
        return lp
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
