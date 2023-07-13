package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.parser.Parser
import com.example.viewdebug.util.ViewDebugInfo
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.getViewDebugInfo
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.collector.getViewUnion
import java.lang.ref.WeakReference

/**
 * 显示拾取view相关信息
 */
class ViewImageCaptureResultDialog(
    ctx: Context,
    private val hostPage: UIPage,
    private val attrIds: HashMap<Int, Pair<String, Parser>>,
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


    fun addAttribute(id: Int, pair: Pair<String, Parser>) {
        attrIds.put(id, pair)
    }

    fun show(title: String, capturedViews: List<View>) {
        dialogBinding.tvHostName.text = title
        if (mode == MODE_IMAGE) {
            showModeImage(capturedViews)
        } else {
            showModeView(capturedViews)
        }
        if (rAdapter.itemCount != 0) {
            hostPage.showDialog(dialogBinding.root)
        }
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
            /*for (attr in u) {
                val attrInfo = attrIds[attr.attributeId]
                if (attrInfo != null) {
                    val item = attrInfo.second.getItem(v, attr.attributeId, attrInfo.first, u, debugInfo)
                    if (item != null) {
                        data.add(item)
                    }
                }
            }*/
        }

        rAdapter.update(data)
    }

    fun close() {
        hostPage.closeDialog(dialogBinding.root)
    }


}
