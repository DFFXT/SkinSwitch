package com.example.viewdebug.util

import android.app.Activity
import android.content.Context
import android.os.Trace
import android.util.Range
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.children
import java.util.LinkedList

internal class ViewCapture {

    fun capture(rootView: View, x: Int, y: Int, vararg excludeViews: View): List<View> {
        val result = LinkedList<View>()
        findViewByPosition(rootView, x, y, false, result, excludeViews = excludeViews)
        return result
    }

    fun capture(context: Context, x: Int, y: Int, vararg excludeViews: View): List<View> {
        val last = getLastWindowRootView(context, x, y, *excludeViews)
        return if (last is ViewGroup) {
            capture(last, x, y, excludeViews = excludeViews)
        } else {
            emptyList()
        }

    }

    companion object {
        private val position = intArrayOf(0, 0)
        private fun inRect(x1: Int, y1: Int, x2: Int, y2: Int, px: Int, py: Int): Boolean {
            return px in (x1 + 1) until x2 && y1 < py && y2 > py
        }

        private fun inRect(view: View, px: Int, py: Int): Boolean {
            view.getLocationOnScreen(position)
            return inRect(
                position[0],
                position[1],
                position[0] + view.measuredWidth,
                position[1] + view.measuredHeight,
                px,
                py,
            )
        }

        /**
         * 获取最后的窗口，必须包含x，y坐标
         * @param x 坐标 当未null时不考虑坐标
         * @param excludeViews 被排除的view，因为调试窗口插件本身会包含两个view
         */
        fun getLastWindowRootView(context: Context, x: Int? = null, y: Int? = null, vararg excludeViews: View): View? {
            try {
                val windowManager = context.getSystemService(WindowManager::class.java)
                val global =
                    Class.forName("android.view.WindowManagerImpl").getDeclaredField("mGlobal")
                        .run {
                            isAccessible = true
                            get(windowManager)
                        }
                val views =
                    Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews")
                        .run {
                            isAccessible = true
                            get(global)
                        } as List<View>

                for (i in views.indices) {
                    val view = views[views.size - 1 - i]
                    if (!excludeViews.contains(view)) {
                        if ((x == null || y == null) || inRect(view, x, y)) {
                            return view
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (context is Activity) {
                    return context.findViewById(android.R.id.content)
                }
            }
            return null
        }
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
        vararg excludeViews: View
    ) {
        rootView.getLocationOnScreen(position)
        if (inRect(
                position[0],
                position[1],
                position[0] + rootView.measuredWidth,
                position[1] + rootView.measuredHeight,
                x,
                y,
            )
            && !excludeViews.contains(rootView)
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
                            excludeViews = excludeViews
                        )
                    }
                } else {
                    out.add(0, rootView)
                }
            }
        }
    }


}
