package com.example.viewdebug.ui

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.R

/**
 * 空白页面
 */
class EmptyPage : UIPage() {

    override fun enableTouch(): Boolean = false

    override fun enableFocus(): Boolean = false

    override fun onCreateTabView(ctx: Context): View {
        return View(ctx).apply {
            val size =
                ctx.resources.getDimensionPixelSize(R.dimen.view_debug_control_ui_status_bar_height)
            layoutParams = ViewGroup.LayoutParams(size, size)
            setBackgroundResource(R.mipmap.view_debug_common_close)
        }
    }

    override fun onCreateContentView(ctx: Context): View {
        return View(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(0 ,0)
        }
    }
}
