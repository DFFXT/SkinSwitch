package com.example.viewdebug.ui.page.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.R
import com.example.viewdebug.apply.ModifyState
import com.example.viewdebug.databinding.ViewDebugItemModifyItemBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.util.copyToClipboard

/**
 * 已应用更改列表item
 */
internal class ModifyItemChildHandle : ItemHandle<ModifyItem>() {


    override fun handle(item: ModifyItem): Boolean {
        return item is ModifyItemChild
    }

    override fun onBindView(item: ModifyItem, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.binding.tvTitle.text = item.name
        vh.binding.tvTitle.setOnClickListener {
            copyToClipboard(it.context, item.name)
        }
        vh.binding.tvState.isVisible = item.state != ModifyState.APPLIED
        if (item.state == ModifyState.REBOOT_UPDATABLE) {
            vh.binding.tvState.text = vh.binding.root.context.getText(R.string.view_debug_dex_reboot_update)
        } else if (item.state == ModifyState.INVALID_APPLY) {
            vh.binding.tvState.text = vh.binding.root.context.getText(R.string.view_debug_dex_invalid_apply)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(ViewDebugItemModifyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private class VH(val binding: ViewDebugItemModifyItemBinding) : RecyclerView.ViewHolder(binding.root)


}