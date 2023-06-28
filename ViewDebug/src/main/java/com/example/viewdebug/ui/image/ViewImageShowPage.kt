package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.R
import com.example.viewdebug.ViewCapture
import com.example.viewdebug.ui.UIPage

/**
 * 显示当前点击处View的背景、图片资源等
 * @param captureAttrId first:属性id @see[android.R.attr.src], second:属性描述
 * @param append 哪种方式设置属性id
 */
class ViewImageShowPage(private val captureAttrId: List<Pair<Int, String>>? = null, private val append: Boolean = true) :
    UIPage() {

    override fun enableTouch(): Boolean = true

    override fun enableFocus(): Boolean = false

    override fun onCreateTabView(ctx: Context): View {
        return View(ctx).apply {
            val size =
                ctx.resources.getDimensionPixelSize(R.dimen.view_debug_control_ui_status_bar_height)
            layoutParams = ViewGroup.LayoutParams(size, size)
            setBackgroundColor(Color.BLUE)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateContentView(ctx: Context): View {
        return View(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            val capture = ViewCapture()
            val dialog = ViewImageCaptureResultDialog(ctx, this@ViewImageShowPage)
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val hostRootView =
                        hostActivity?.get()?.window?.decorView
                    hostRootView ?: return@setOnTouchListener true
                    val capturedViews =
                        capture.capture(hostRootView, event.rawX.toInt(), event.rawY.toInt())
                    dialog.show(capturedViews)
                }
                return@setOnTouchListener true
            }
        }
    }
}
