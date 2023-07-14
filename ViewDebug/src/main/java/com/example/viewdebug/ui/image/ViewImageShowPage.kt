package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.R
import com.example.viewdebug.ViewCapture
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.parser.Parser
import com.example.viewdebug.ui.image.parser.ReferenceParser
import com.example.viewdebug.ui.image.parser.TextColorParser
import com.example.viewdebug.util.enableSelect

/**
 * 显示当前点击处View的背景、图片资源等
 * @param captureAttrId first:属性id @see[android.R.attr.src], second:属性描述
 * @param append 哪种方式设置属性id
 */
class ViewImageShowPage(
    private val captureAttrId: ArrayList<Pair<Int, Pair<String, Parser>>> = ArrayList(),
) :
    UIPage() {

    private val attribute by lazy {
        val attrIds = HashMap<Int, Pair<String, Parser>>()
        attrIds.put(android.R.attr.background, Pair("background", ReferenceParser))
        attrIds.put(android.R.attr.src, Pair("src", ReferenceParser))
        attrIds.put(android.R.attr.foreground, Pair("foreground", ReferenceParser))
        attrIds.put(android.R.attr.drawableStart, Pair("drawableStart", ReferenceParser))
        attrIds.put(android.R.attr.drawableTop, Pair("drawableTop", ReferenceParser))
        attrIds.put(android.R.attr.drawableEnd, Pair("drawableEnd", ReferenceParser))
        attrIds.put(android.R.attr.drawableBottom, Pair("drawableBottom", ReferenceParser))
        attrIds.put(android.R.attr.thumb, Pair("thumb", ReferenceParser))
        attrIds.put(android.R.attr.button, Pair("button", ReferenceParser))
        attrIds.put(android.R.attr.text, Pair("text", TextColorParser))
        attrIds
    }
    private var dialog: ViewImageCaptureResultDialog? = null

    override fun enableTouch(): Boolean = true

    override fun enableFocus(): Boolean = true

    override fun onCreateTabView(ctx: Context): View {
        return View(ctx).apply {
            val size =
                ctx.resources.getDimensionPixelSize(R.dimen.view_debug_control_ui_status_bar_height)
            layoutParams = ViewGroup.LayoutParams(size, size)
            setBackgroundResource(R.mipmap.view_debug_image_layer_pick)
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            // enablePress()
            enableSelect()
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
            dialog = ViewImageCaptureResultDialog(ctx, this@ViewImageShowPage, attribute)
            captureAttrId.forEach {
                dialog?.addAttribute(it.first, it.second)
            }
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val hostRootView =
                        hostActivity?.get()?.window?.decorView
                    hostRootView ?: return@setOnTouchListener true
                    val capturedViews =
                        capture.capture(hostRootView, event.rawX.toInt(), event.rawY.toInt())
                    // activity名称
                    val hostName = hostActivity?.get()?.javaClass?.simpleName ?: ""
                    dialog?.show(hostName, capturedViews)
                }
                return@setOnTouchListener true
            }
        }
    }

    fun addAttribute(id: Int, name: String, parser: Parser) {
        attribute[id] = Pair(name, parser)
    }

    fun removeAttribute(id: Int) {
        attribute.remove(id)
    }

    override fun onClose() {
        super.onClose()
        dialog?.close()
    }
}
