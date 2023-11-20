package com.example.viewdebug

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import java.util.LinkedList

internal class ViewCapture {
    private val position = intArrayOf(0, 0)
    fun capture(rootView: View, x: Int, y: Int): List<View> {
        val result = LinkedList<View>()
        findViewByPosition(rootView, x, y, false, result)
        return result
    }

    /**
     * 捕获x，y坐标的View
     * @param captureInvisible 是否包含隐藏view
     */
    private fun findViewByPosition(
        rootView: View,
        x: Int,
        y: Int,
        captureInvisible: Boolean,
        out: LinkedList<View>,
    ) {
        rootView.getLocationInWindow(position)
        if (inRect(
                position[0],
                position[1],
                position[0] + rootView.measuredWidth,
                position[1] + rootView.measuredHeight,
                x,
                y,
            )
        ) {
            if (captureInvisible || rootView.isShown) {
                if (rootView is ViewGroup) {
                    out.add(0, rootView)
                    rootView.children.forEach { v ->
                        findViewByPosition(
                            v,
                            x,
                            y,
                            captureInvisible,
                            out,
                        )
                    }
                } else {
                    out.add(0, rootView)
                }
            }
        }
    }

    private fun inRect(x1: Int, y1: Int, x2: Int, y2: Int, px: Int, py: Int): Boolean {
        return px in (x1 + 1) until x2 && y1 < py && y2 > py
    }
}
