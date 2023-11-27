package com.example.viewdebug.ui.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.page.itemHanlder.ImageItemHandler
import com.example.viewdebug.ui.page.itemHanlder.Item
import com.example.viewdebug.ui.page.parser.Parser
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.getViewDebugInfo
import com.skin.skincore.collector.getViewUnion
import java.lang.ref.WeakReference

/**
 * 显示拾取view相关信息
 */
class ViewImageCaptureResultDialog(
    ctx: Context,
    hostPage: UIPage,
    private val attrIds: HashMap<Int, Pair<String, Parser>>,
) : BaseDialog(hostPage) {
    private val imageItemHandler = ImageItemHandler(hostPage)
    private val rAdapter = MultiTypeRecyclerAdapter<Item>()
    private lateinit var dialogBinding: ViewDebugImageSetContainerBinding

    companion object {
        private var mode = 0
        private var MODE_IMAGE = 0
        private var MODE_VIEW = 1
    }


    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        dialogBinding =
            ViewDebugImageSetContainerBinding.inflate(
                LayoutInflater.from(ctx),
                parent,
                false,
            )
        adjustOrientation(dialogBinding.root)
        rAdapter.registerItemHandler(imageItemHandler)
        dialogBinding.rvImage.adapter = rAdapter
        dialogBinding.ivClose.setOnClickListener {
            close()
        }
        return dialogBinding.root
    }


    fun addAttribute(id: Int, pair: Pair<String, Parser>) {
        attrIds.put(id, pair)
    }

    fun show(title: String, capturedViews: List<View>) {

        if (mode == MODE_IMAGE) {
            showModeImage(capturedViews)
        } else {
            showModeView(capturedViews)
        }
        if (rAdapter.itemCount != 0) {
            show()
            dialogBinding.tvHostName.text = title
        }
    }

    /**
     * 获取第一个显示的图片
     */
    fun getFirstShowView(): View? {
        return rAdapter.data.firstOrNull()?.target?.get()
    }

    private fun showModeView(capturedViews: List<View>) {
        /*val data = capturedViews.map {
            ImageItemHandler.ViewItem(
                WeakReference(it),
                it.getViewUnion(),
                it.getViewDebugInfo(),
            )
        }*/
        // rAdapter.update(data)
    }

    /**
     * @param target 优先级最高的view
     */
    private fun showModeImage(capturedViews: List<View>) {
        val data = ArrayList<Item>()
        for (v in capturedViews) {
            val u = v.getViewUnion()
            val debugInfo = v.getViewDebugInfo()
            attrIds.forEach {
                val attrInfo = it.value
                val item = attrInfo.second.getItem(v, it.key, attrInfo.first, u, debugInfo)
                if (item != null) {
                    data.add(item)
                }
            }
        }
        // 如果没有抓取到试图，则返回第一个和面积最小的一个
        if (data.isEmpty()) {
            val firstView = capturedViews.firstOrNull()
            val first = createSimpleViewItem(capturedViews.firstOrNull())
            val sortedViews = capturedViews.sortedBy { it.measuredHeight * it.measuredWidth }
            if (first != null) {
                data.add(first)
            }
            for (v in sortedViews) {
                if (v != firstView) {
                    val item = createSimpleViewItem(v)
                    if (item != null) {
                        data.add(item)
                        break
                    }
                }
            }

        }

        rAdapter.update(data)
    }

    private fun createSimpleViewItem(v: View?): Item? {
        v ?: return null
        val u = v.getViewUnion()
        val debugInfo = v.getViewDebugInfo() ?: return null
        val ln = debugInfo.getLayoutName(v.resources) ?: return null
        return Item(
            WeakReference(v),
            R.mipmap.view_debug_view_view_type_icon,
            ln,
            0,
            v::class.java.simpleName
        )
    }


}
