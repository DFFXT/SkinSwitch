package com.example.viewdebug.ui.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.ui.UIPage

class XmlTextDialog(
    private val ctx: Context,
    private val hostPage: UIPage,
) {
    private val binding = ViewDebugXmlTextContainerBinding.inflate(LayoutInflater.from(ctx), hostPage.tabView.parent as ViewGroup, false)

    init {
        binding.tvText.movementMethod = LinkMovementMethod.getInstance()
    }
    fun show(title: String, xml: CharSequence) {
        binding.tvName.text = title
        binding.tvText.text = xml
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
