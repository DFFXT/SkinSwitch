package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugImageSetContainerBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.image.itemHanlder.ImageItemHandler
import com.example.viewdebug.ui.image.itemHanlder.Item
import com.example.viewdebug.ui.image.parser.Parser
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.getViewDebugInfo
import com.skin.skincore.collector.getViewUnion

/**
 * 显示拾取view相关信息
 */
class ViewImageCaptureResultDialog(
    ctx: Context,
    hostPage: UIPage,
    private val attrIds: HashMap<Int, Pair<String, Parser>>,
) : BaseDialog(hostPage) {
    private val imageItemHandler = ImageItemHandler(hostPage)
    private val rAdapter = MultiTypeRecyclerAdapter<Any>()
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


}
