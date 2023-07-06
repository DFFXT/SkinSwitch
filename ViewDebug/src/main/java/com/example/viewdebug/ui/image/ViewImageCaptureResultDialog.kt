package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.getViewDebugInfo
import com.skin.skincore.collector.getViewUnion
import java.lang.ref.WeakReference

/**
 * 显示拾取view相关信息
 */
class ViewImageCaptureResultDialog(
    ctx: Context,
    private val hostPage: UIPage,
    private val attrIds: HashMap<Int, String>,
) {
    private val imageItemHandler = ImageItemHandler(hostPage)
    private val rAdapter = MultiTypeRecyclerAdapter<Any>()
    private val dialogBinding: ViewDebugImageSetContainerBinding

    companion object {
        private var mode = 0
        private var MODE_IMAGE = 0
        private var MODE_VIEW = 1
    }

    init {

        dialogBinding =
            ViewDebugImageSetContainerBinding.inflate(
                LayoutInflater.from(ctx),
                hostPage.tabView.parent as ViewGroup,
                false,
            )
        adjustOrientation(dialogBinding.root)
        dialogBinding.rvImage.adapter = rAdapter
        rAdapter.registerItemHandler(imageItemHandler)
    }



    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
    }

    fun addAttribute(id: Int, name: String) {
        attrIds.put(id, name)
    }

    fun show(title: String, capturedViews: List<View>) {
        dialogBinding.tvHostName.text = title
        if (mode == MODE_IMAGE) {
            showModeImage(capturedViews)
        } else {
            showModeView(capturedViews)
        }
    }

    private fun showModeView(capturedViews: List<View>) {
        capturedViews.map {
            ImageItemHandler.ViewItem(
                WeakReference(it),
                it.getViewUnion(),
                it.getViewDebugInfo(),
            )
        }
    }

    private fun showModeImage(capturedViews: List<View>) {
        val data = ArrayList<ImageItemHandler.Item>()
        for (v in capturedViews) {
            val u = v.getViewUnion() ?: continue
            val debugInfo = v.getViewDebugInfo()
            val layoutId = debugInfo?.layoutId ?: 0
            val layoutInfo = if (layoutId == 0) {
                // 没有布局信息，直接new的对象
                "未知：" + v::class.java.name
            } else {
                v.context.resources.getResourceEntryName(layoutId) + ".xml"
            }
            for (attr in u) {
                attrIds.forEach {
                    if (it.key == attr.attributeId) {
                        data.add(ImageItemHandler.Item(WeakReference(v), attr.resId, layoutId, layoutInfo, it.value))
                    }
                }
            }
        }

        if (data.isNotEmpty()) {
            rAdapter.update(data)
            hostPage.showDialog(dialogBinding.root)
        }
    }

    fun close() {
        hostPage.closeDialog(dialogBinding.root)
    }
}
