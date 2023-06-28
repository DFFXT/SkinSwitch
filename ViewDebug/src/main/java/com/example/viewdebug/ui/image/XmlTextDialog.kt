package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugXmlTextContainerBinding
import com.example.viewdebug.ui.UIPage

class XmlTextDialog(
    ctx: Context,
    private val hostPage: UIPage,
) {
    private val binding = ViewDebugXmlTextContainerBinding.inflate(LayoutInflater.from(ctx), hostPage.tabView.parent as ViewGroup, false)

    fun show(title: String, xml: CharSequence) {
        binding.tvName.text = title
        binding.tvText.text = xml
        hostPage.showDialog(binding.root)
    }
}
