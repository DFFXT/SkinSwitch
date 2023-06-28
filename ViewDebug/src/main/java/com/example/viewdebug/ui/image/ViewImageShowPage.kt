package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.R
import com.example.viewdebug.ViewCapture
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.ui.UIPage
import com.skin.skincore.collector.getViewUnion

/**
 * 显示当前点击处View的背景、图片资源等
 * @param captureAttrId first:属性id @see[android.R.attr.src], second:属性描述
 * @param append 哪种方式设置属性id
 */
class ViewImageShowPage(captureAttrId: List<Pair<Int, String>>? = null, append: Boolean = true) : UIPage() {
    private val attrIds = ArrayList<Pair<Int, String>>()

    init {
        if (captureAttrId == null || append) {
            attrIds.add(Pair(android.R.attr.background, "background"))
            attrIds.add(Pair(android.R.attr.src, "background"))
            attrIds.add(Pair(android.R.attr.foreground, "foreground"))
            attrIds.add(Pair(android.R.attr.drawableStart, "drawableStart"))
            attrIds.add(Pair(android.R.attr.drawableTop, "drawableTop"))
            attrIds.add(Pair(android.R.attr.drawableEnd, "drawableEnd"))
            attrIds.add(Pair(android.R.attr.drawableBottom, "drawableBottom"))
            attrIds.add(Pair(android.R.attr.thumb, "thumb"))
            attrIds.add(Pair(android.R.attr.button, "button"))
        }
        if (captureAttrId != null) {
            attrIds.addAll(captureAttrId)
        }
    }

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
            val adapter = ImageAdapter()
            val dialogBinding = ViewDebugImageSetContainerBinding.inflate(LayoutInflater.from(ctx), tabView.parent as ViewGroup, false)
            dialogBinding.rvImage.adapter = adapter
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val hostRootView =
                        hostActivity?.get()?.window?.decorView
                    hostRootView ?: return@setOnTouchListener true
                    val capturedViews =
                        capture.capture(hostRootView, event.rawX.toInt(), event.rawY.toInt())
                    val data = ArrayList<ImageAdapter.Item>()
                    for (v in capturedViews) {
                        val u = v.getViewUnion() ?: continue
                        for (attr in u) {
                            attrIds.forEach {
                                if (it.first == attr.attributeId) {
                                    data.add(ImageAdapter.Item(attr.resId, it.second))
                                }
                            }
                        }
                    }
                    if (data.isNotEmpty()) {
                        adapter.update(data)
                        showDialog(dialogBinding.root)
                    }
                }
                return@setOnTouchListener true
            }
        }
    }
}
