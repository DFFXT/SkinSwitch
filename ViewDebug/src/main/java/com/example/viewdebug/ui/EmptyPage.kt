package com.example.viewdebug.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import com.example.viewdebug.R
import com.example.viewdebug.util.enablePress

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
            layoutParams = ViewGroup.MarginLayoutParams(size, size)
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart = ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_ui_control_button_margin)
                marginEnd = ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_ui_control_button_margin)
            }
            setBackgroundResource(R.mipmap.view_debug_common_close)
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            enablePress()
        }
    }

    override fun onCreateContentView(ctx: Context): View {
        return View(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(0 ,0)
        }
    }
}
