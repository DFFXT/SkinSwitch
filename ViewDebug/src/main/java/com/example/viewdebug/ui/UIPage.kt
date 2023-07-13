package com.example.viewdebug.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import com.example.viewdebug.ui.dialog.BaseDialog
import java.lang.ref.WeakReference

abstract class UIPage {
    var isOnShow = false
        private set

    lateinit var tabView: View
    lateinit var contentView: ViewGroup
    val ctx by lazy { tabView.context }

    // 当前activity
    internal var hostActivity: WeakReference<Activity>? = null
    internal fun createTabView(ctx: Context): View {
        if (!this::tabView.isInitialized) {
            tabView = onCreateTabView(ctx)
        }
        return tabView
    }

    internal fun createContentView(ctx: Context): View {
        if (!this::contentView.isInitialized) {
            contentView = FrameLayout(ctx)
            contentView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            contentView.addView(onCreateContentView(ctx))
        }
        return contentView
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showDialog(dialog: BaseDialog, dismissCallback: Runnable? = null) {
        val dialogView = dialog.dialogView
        if (dialogView.parent != null) return
        val container = FrameLayout(dialogView.context)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        container.addView(dialogView)
        val lp = dialogView.layoutParams as? FrameLayout.LayoutParams
        if (lp != null) {
            lp.gravity = Gravity.CENTER
            dialogView.layoutParams = lp
        }
        contentView.addView(container)
        if (dialog.clickClose()) {
            container.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    dismissCallback?.run()
                    closeDialog(dialog)
                }
                return@setOnTouchListener true
            }
        } else {
            container.isClickable = true
            container.isFocusable = true
        }
        val b = dialog.background()
        if (b != null) {
            container.background = b
        }
    }

    fun closeDialog(dialog: BaseDialog) {
        if (dialog.dialogView.parent != null) {
            val p = dialog.dialogView.parent as ViewGroup
            contentView.removeView(p)
            p.removeView(dialog.dialogView)
        }
    }

    /**
     * 当前activity发生变更
     */
    open fun onHostActivityChange(hostActivity: WeakReference<Activity>) {
        this.hostActivity = hostActivity
    }

    open fun enableTouch(): Boolean = true
    open fun enableFocus(): Boolean = false
    abstract fun onCreateTabView(ctx: Context): View
    abstract fun onCreateContentView(ctx: Context): View

    @CallSuper
    open fun onShow() {
        tabView.isSelected = true
        isOnShow = true
    }

    @CallSuper
    open fun onClose() {
        tabView.isSelected = false
        isOnShow = false
    }

    @CallSuper
    open fun onDestroy() {
    }
}
