package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.getViewDebugInfo
import com.example.viewdebug.util.setSize
import com.skin.skincore.collector.getViewUnion

/**
 * 显示拾取view相关信息
 */
class ViewImageCaptureResultDialog(
    ctx: Context,
    private val hostPage: UIPage,
    private val attrIds: HashMap<Int, String>,
) {
    private val adapter = ImageAdapter()
    private val dialogBinding: ViewDebugImageSetContainerBinding

    init {
        adapter.onImageClick = {
            val dialog = ImageDetailDialog(hostPage)
            dialog.show(it.id)
        }
        adapter.onLayoutNameClick = {
            copyToClipboard(ctx, it.layoutName)
            tryShowXmlText(ctx, it.layoutId)
        }
        adapter.onAttributeNameClick = {
            tryShowXmlText(ctx, it.id)
        }
        dialogBinding =
            ViewDebugImageSetContainerBinding.inflate(
                LayoutInflater.from(ctx),
                hostPage.tabView.parent as ViewGroup,
                false,
            )
        adjustOrientation(dialogBinding.root)
        dialogBinding.rvImage.adapter = adapter
    }

    private fun tryShowXmlText(ctx: Context, id: Int) {
        val attrValue = ctx.resources.getResourceEntryName(id)
        try {
            val parsedValue = XmlParser().getXmlText(ctx, id) { text ->
                copyToClipboard(ctx, text)
            }
            val dialog = XmlTextDialog(ctx, hostPage)
            dialog.show(id, parsedValue)
        } catch (e: Exception) {
            copyToClipboard(ctx, attrValue)
        }
    }

    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
    }

    fun addAttribute(id: Int, name: String) {
        attrIds.put(id, name)
    }

    fun show(capturedViews: List<View>) {
        val data = ArrayList<ImageAdapter.Item>()
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
                        data.add(ImageAdapter.Item(attr.resId, layoutId, layoutInfo, it.value))
                    }
                }
            }
        }
        if (data.isNotEmpty()) {
            adapter.update(data)
            hostPage.showDialog(dialogBinding.root)
        }
    }

    fun close() {
        hostPage.closeDialog(dialogBinding.root)
    }
}
