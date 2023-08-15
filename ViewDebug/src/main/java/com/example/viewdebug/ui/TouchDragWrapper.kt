package com.example.viewdebug.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import com.example.viewdebug.R
import com.example.viewdebug.util.touch.BaseDragHelper

/**
 * view拖动
 * @param moveTarget 被移动的view，必须是windowManager下的view
 * @param touchTarget 被触发的view
 */
class TouchDragWrapper(private val moveTarget: View, touchTarget: View) {
    private val ctx = moveTarget.context
    private val wm = ctx.getSystemService(WindowManager::class.java)
    init {
        object : BaseDragHelper() {
            override fun longClickMove(dx: Float, dy: Float) {
                offset(dx, dy, moveTarget.layoutParams)
            }

            override fun longOnClick(x: Float, y: Float): Boolean {
                moveTarget.foreground = AppCompatResources.getDrawable(ctx, R.drawable.view_debug_common_rect_stroke_1dp)
                return false
            }

            override fun longClickUpMoved(x: Float, y: Float) {
                super.longClickUpMoved(x, y)
                moveTarget.foreground = null
            }

            override fun longClickUpNoMove(x: Float, y: Float) {
                super.longClickUpNoMove(x, y)
                moveTarget.foreground = null
            }
        }.attachToView(touchTarget)
    }
    private fun offset(x: Float, y: Float, lp: ViewGroup.LayoutParams?) {
        if (lp !is WindowManager.LayoutParams) return
        // 根据gravity确定能够移动的范围，gravity不支持left和right，请使用start和end
        var xFactory = 1
        var yFactory = 1
        var minStart = 0
        var minTop = 0
        var maxEnd = ctx.resources.displayMetrics.widthPixels - moveTarget.measuredWidth
        var maxBottom = ctx.resources.displayMetrics.heightPixels - moveTarget.measuredHeight
        if (lp.gravity and Gravity.START == Gravity.START) {
            xFactory = 1
        } else if (lp.gravity and Gravity.END == Gravity.END){
            xFactory = -1
        } else {
            minStart = -(ctx.resources.displayMetrics.widthPixels - moveTarget.measuredWidth)/2
            maxEnd = -minStart
        }

        if (lp.gravity and Gravity.TOP == Gravity.TOP) {
            yFactory = 1
        } else if (lp.gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            yFactory = -1
        } else {
            minTop = -(ctx.resources.displayMetrics.heightPixels - moveTarget.height)/2
            maxBottom = -minTop
        }

        lp.x += x.toInt() * xFactory
        lp.y += y.toInt() * yFactory
        // 范围限制
        if (lp.x < minStart) {
            lp.x = minStart
        }
        if (lp.x > maxEnd) {
            lp.x = maxEnd
        }
        if (lp.y < minTop) {
            lp.y = minTop
        }
        if (lp.y > maxBottom) {
            lp.y = maxBottom
        }
        if (moveTarget.isAttachedToWindow) {
            wm.updateViewLayout(moveTarget, lp)
        }
    }
}