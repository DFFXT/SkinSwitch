package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.adjustOrientation
import java.io.File

class XmlTextDialog(
    private val ctx: Context,
    private val hostPage: UIPage,
) {
    private val binding = ViewDebugXmlTextContainerBinding.inflate(
        LayoutInflater.from(ctx),
        hostPage.tabView.parent as ViewGroup,
        false,
    )
    private var mode = 0

    private var layoutId: Int = 0
    private lateinit var originText: CharSequence

    init {
        binding.tvText.movementMethod = LinkMovementMethod.getInstance()
        adjustOrientation(binding.container)
        binding.ivXmlTextOperate.setOnClickListener {
            Toast.makeText(it.context, "未实现该功能", Toast.LENGTH_LONG).show()
            return@setOnClickListener
            if (mode == 0) {
                binding.tvText.requestFocus()
                binding.tvText.selectAll()
                onModeChange(1)
            } else if (mode == 1) {
                onModeChange(0)
                binding.tvText.clearFocus()
                saveChange()
            }
        }
        binding.ivClose.setOnClickListener {
            onModeChange(0)
            if (binding.tvText.text.toString() != originText) {
                binding.tvText.setText(originText)
            }
        }
    }

    private fun onModeChange(mode: Int) {
        this.mode = mode
        binding.ivClose.isVisible = mode == 1
        binding.ivXmlTextOperate.isSelected = mode == 1
    }

    private fun saveChange() {
        // todo 需要将xml编译为AXML格式文件，然后才能动态加载，目前还未实现，理论上能在android上生成，需要R文件参与编译
       /* val dirPath = ctx.externalCacheDir!!.absolutePath + File.separator + "layout"
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, layoutId.toString())
        file.writeText(originText.toString())
        ViewDebugMergeResource.layoutInterceptorMapper.add(layoutId)*/
    }

    fun show(layoutId: Int, xml: CharSequence) {
        val attrValue = ctx.resources.getResourceEntryName(layoutId)
        val title = ctx.resources.getResourceTypeName(layoutId) + "/" + attrValue
        this.layoutId = layoutId
        this.originText = xml
        binding.tvName.text = title
        binding.tvText.setText(xml)
        binding.tvName.setOnClickListener {
            copyToClipboard(ctx, title)
        }
        hostPage.showDialog(binding.root)
    }

    private fun copyToClipboard(ctx: Context, text: String) {
        val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
    }

    fun close() {
        hostPage.closeDialog(binding.root)
    }
}
