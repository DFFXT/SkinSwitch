package com.example.viewdebug.ui

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.example.viewdebug.databinding.LayoutViewDebugUiControlBinding
import com.example.viewdebug.databinding.ViewDebugLayoutMainContentBinding
import com.skin.log.Logger
import java.lang.ref.WeakReference

/**
 * ui 控制
 */
class UIControl(private val ctx: Context) {
    // 顶部控制区域
    private val uiControlBinding by lazy {
        val binding = LayoutViewDebugUiControlBinding.inflate(LayoutInflater.from(ctx))
        TouchDragWrapper(binding.root, binding.root)
        binding
    }

    // 内容区域
    private val contentBinding by lazy {
        ViewDebugLayoutMainContentBinding.inflate(LayoutInflater.from(ctx))
    }
    private val pages = ArrayList<UIPage>()
    private val wm by lazy { ctx.getSystemService(WindowManager::class.java) }

    private var hostActivity: WeakReference<Activity>? = null

    private var config: UiControlConfig = UiControlConfig()

    // 是否显示
    var isShown = false
        private set

    fun show() {
        if (!Settings.canDrawOverlays(ctx)) {
            Logger.e("UIControl", "no overlay permission")
            return
        }
        if (isShown) return
        // 添加内容区域
        var contentLp = contentBinding.root.layoutParams
        if (contentLp == null) {
            contentLp = getLayoutParams()
            contentLp.width = ctx.resources.displayMetrics.widthPixels
            contentLp.height = ctx.resources.displayMetrics.heightPixels
            contentLp.flags = contentLp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        wm.addView(contentBinding.root, contentLp)
        // 添加控制栏
        var lp = uiControlBinding.root.layoutParams
        if (lp == null) {
            lp = getLayoutParams()
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        tryUpdate(lp)
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
        if (!Settings.canDrawOverlays(ctx)) {
            Logger.e("UIControl", "no overlay permission")
            return
        }
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
            // 根据页面配置焦点和触摸状态
            if (!delegate.enableTouch()) {
                lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            } else {
                lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
            }
            if (!delegate.enableFocus()) {
                lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            } else {
                lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
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
        lp.flags =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
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
     * 更新看控制栏位置
     */
    fun updatePosition(uiControlConfig: UiControlConfig) {
        this.config = uiControlConfig
        tryUpdate(uiControlBinding.root.layoutParams)
    }

    private fun tryUpdate(lp: ViewGroup.LayoutParams?) {
        if (lp !is WindowManager.LayoutParams) return
        lp.gravity = config.gravity
        lp.x = config.offsetX
        lp.y = config.offsetY
        if (uiControlBinding.root.isAttachedToWindow) {
            wm.updateViewLayout(uiControlBinding.root, lp)
        }
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
