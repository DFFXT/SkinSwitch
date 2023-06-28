package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.ui.UIPage
import com.skin.skincore.collector.getViewUnion

/**
 * 显示拾取view相关信息
 */
internal class ViewImageCaptureResultDialog(
    ctx: Context,
    private val hostPage: UIPage,
    captureAttrId: List<Pair<Int, String>>? = null,
    append: Boolean = true,
) {
    private val adapter = ImageAdapter()
    private val dialogBinding: ViewDebugImageSetContainerBinding
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
        adapter.onAttributeNameClick = {
            try {
                val parsedValue = XmlParser().getXmlText(ctx, it.id)
                val dialog = XmlTextDialog(ctx, hostPage)
                val name = ctx.resources.getResourceTypeName(it.id) + "/" +ctx.resources.getResourceEntryName(it.id)
                dialog.show(name, parsedValue)
            } catch (e: Exception) {
            }
        }
        dialogBinding =
            ViewDebugImageSetContainerBinding.inflate(
                LayoutInflater.from(ctx),
                hostPage.tabView.parent as ViewGroup,
                false,
            )
        dialogBinding.rvImage.adapter = adapter
    }

    fun show(capturedViews: List<View>) {
        val data = ArrayList<ImageAdapter.Item>()
        for (v in capturedViews) {
            val u = v.getViewUnion() ?: continue
            val layout = v.context.resources.getResourceEntryName(u.layoutId)
            for (attr in u) {
                attrIds.forEach {
                    if (it.first == attr.attributeId) {
                        data.add(ImageAdapter.Item(attr.resId, "$layout.xml", it.second))
                    }
                }
            }
        }
        if (data.isNotEmpty()) {
            adapter.update(data)
            hostPage.showDialog(dialogBinding.root)
        }
    }
}
