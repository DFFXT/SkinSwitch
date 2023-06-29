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
import java.lang.ref.WeakReference

abstract class UIPage {
    var isOnShow = false
        private set

    lateinit var tabView: View
    lateinit var contentView: ViewGroup

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
    fun showDialog(dialog: View, dismissCallback: Runnable? = null) {
        if (dialog.parent != null) return
        val container = FrameLayout(dialog.context)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        container.addView(dialog)
        val lp = dialog.layoutParams as? FrameLayout.LayoutParams
        if (lp != null) {
            lp.gravity = Gravity.CENTER
            dialog.layoutParams = lp
        }
        contentView.addView(container)
        container.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                dismissCallback?.run()
                closeDialog(dialog)
            }
            return@setOnTouchListener true
        }
    }

    fun closeDialog(dialog: View) {
        if (dialog.parent != null) {
            val p = dialog.parent as ViewGroup
            contentView.removeView(p)
            p.removeView(dialog)
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
