package com.example.viewdebug.ui.page.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugItemModifyItemBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.util.copyToClipboard

/**
 * 已应用更改列表item
 */
internal class ModifyItemHandle : ItemHandle<ModifyItemHandle.ModifyItem>() {


    override fun handle(item: ModifyItem): Boolean {
        return true
    }

    override fun onBindView(item: ModifyItem, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.binding.tvTitle.text = item.name
        vh.binding.tvTitle.setOnClickListener {
            copyToClipboard(it.context, item.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(ViewDebugItemModifyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private class VH(val binding: ViewDebugItemModifyItemBinding) : RecyclerView.ViewHolder(binding.root)

    class ModifyItem(val name: String, val id: Int, val type: String)
}