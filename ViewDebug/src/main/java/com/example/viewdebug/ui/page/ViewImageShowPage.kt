package com.example.viewdebug.ui.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.example.viewdebug.R
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.page.layout.LayoutProfilerView
import com.example.viewdebug.ui.page.parser.AttrTextParser
import com.example.viewdebug.ui.page.parser.Parser
import com.example.viewdebug.ui.page.parser.ReferenceParser
import com.example.viewdebug.util.ViewCapture
import com.fxf.debugwindowlibaray.ui.UIPage
import java.util.LinkedList

/**
 * 显示当前点击处View的背景、图片资源等
 * @param captureAttrId first:属性id @see[android.R.attr.src], second:属性描述
 * @param append 哪种方式设置属性id
 */
class ViewImageShowPage(
    private val captureAttrId: ArrayList<Pair<Int, Pair<String, Parser>>> = ArrayList(),
) :
    UIPage() {

    private val profilerView by lazy {
        LayoutProfilerView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

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
        attrIds.put(android.R.attr.text, Pair("text", AttrTextParser))
        attrIds
    }
    private var dialog: ViewImageCaptureResultDialog? = null

    private var mode = ViewImageCaptureResultDialog.MODE_VIEW

    // 调试插件的两个view
    private val rootParentViews by lazy {
        arrayOf(WindowControlManager.getControlRoot(), WindowControlManager.getContentRoot())
    }

    override fun enableTouch(): Boolean = true

    override fun enableFocus(): Boolean = true

    override fun onCreateTabView(ctx: Context, parent: ViewGroup): View {
        val container = FrameLayout(ctx)
        val iv = super.onCreateTabView(ctx, parent).apply {
            this as ImageView
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setOnClickListener {
                if (this@ViewImageShowPage.isOnShow) {
                    mode = if (mode == ViewImageCaptureResultDialog.MODE_IMAGE) {
                        imageTintList = ColorStateList.valueOf(Color.WHITE)
                        ViewImageCaptureResultDialog.MODE_VIEW
                    } else {
                        imageTintList = ColorStateList.valueOf(Color.RED)
                        ViewImageCaptureResultDialog.MODE_IMAGE
                    }
                }
                container.callOnClick()
            }
        }
        container.addView(iv)
        return container
    }
    override fun getTabIcon(): Int = R.mipmap.view_debug_image_layer_pick

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        return profilerView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            val capture = ViewCapture()
            dialog = ViewImageCaptureResultDialog(ctx, this@ViewImageShowPage, attribute) {
                profilerView.highlight(it)
            }
            captureAttrId.forEach {
                dialog?.addAttribute(it.first, it.second)
            }
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val hostRootView =
                        hostActivity?.get()?.window?.decorView
                    hostRootView ?: return@setOnTouchListener true
                    val capturedViews =
                        capture.capture(hostRootView.context, event.rawX.toInt(), event.rawY.toInt(), excludeViews = rootParentViews)



                    // activity名称
                    val hostName = hostActivity?.get()?.javaClass?.simpleName ?: ""
                    dialog?.show(hostName, capturedViews, mode)
                    val target = dialog?.getFirstShowView()
                    profilerView.highlight(target = target)
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

    override fun onShow() {
        super.onShow()
        val activity = hostActivity?.get() ?: return
        // val root = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val root = ViewCapture.getLastWindowRootView(activity, excludeViews = rootParentViews) ?: return
        if (root !is ViewGroup) return
        val list = LinkedList<View>()
        getChildren(root, list, excludeViews = rootParentViews)
        profilerView.update(list)
        profilerView.highlight(null)
    }

    private fun getRootParent(view: View): View {
        return when (val parent = view.parent) {
            null -> {
                view
            }
            is View -> {
                getRootParent(parent)
            }

            else -> view
        }
    }

    private fun getChildren(view: View, out: MutableList<View>, vararg excludeViews: View) {
        if (view.isShown && !excludeViews.contains(view)) {
            out.add(view)
            if (view is ViewGroup) {
                view.children.forEach {
                    getChildren(it, out, excludeViews = excludeViews)
                }
            }
        }
    }

    override fun onClose() {
        super.onClose()
        dialog?.close()
    }
}
