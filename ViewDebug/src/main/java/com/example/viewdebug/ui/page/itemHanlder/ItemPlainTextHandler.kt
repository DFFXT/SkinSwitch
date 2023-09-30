package com.example.viewdebug.ui.page.itemHanlder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugItemPlainTextBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.util.copyToClipboard

/**
 * 纯text显示
 */
class ItemPlainTextHandler(private val click: ((String) -> Unit)? = null): ItemHandle<String>() {
    override fun handle(item: String): Boolean {
        return true
    }

    override fun onBindView(item: String, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.binding.tvText.text = item
        vh.binding.tvText.setOnClickListener {
            copyToClipboard(vh.binding.root.context, item)
            click?.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(ViewDebugItemPlainTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getViewType() = 4

    private class VH(val binding: ViewDebugItemPlainTextBinding) : RecyclerView.ViewHolder(binding.root)

}