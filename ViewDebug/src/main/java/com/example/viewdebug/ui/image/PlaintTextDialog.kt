package com.example.viewdebug.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewdebug.databinding.ViewDebugLayoutPlainTextBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.image.itemHanlder.ItemPlainTextHandler
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.copyToClipboard

/**
 * 显示纯文本，以行显示
 */
class PlaintTextDialog(host: UIPage) : BaseDialog(host) {
    private lateinit var binding: ViewDebugLayoutPlainTextBinding
    private val adapter = MultiTypeRecyclerAdapter<String>().apply {
        registerItemHandler(ItemPlainTextHandler {
            // 截断行号，方便Android studio定位
            val text = it.subSequence(0, it.indexOf(":") - 1)
            copyToClipboard(ctx = host.ctx, text.toString())
        })
    }
    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        binding = ViewDebugLayoutPlainTextBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.rvList.adapter = adapter
        adjustOrientation(binding.root)
        return binding.root
    }

    fun show(text: String) {
        show()
        adapter.update(text.split("\n"))
    }

}