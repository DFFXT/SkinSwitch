package com.example.viewdebug.ui.page.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.R
import com.example.viewdebug.apply.ModifyState
import com.example.viewdebug.databinding.ViewDebugItemModifyItemParentBinding
import com.example.viewdebug.rv.ItemHandle

class ModifyItemParentHandle(private val itemClick: (item: ModifyItemParent, index: Int) -> Unit) : ItemHandle<ModifyItem>() {
    override fun handle(item: ModifyItem): Boolean {
        return item is ModifyItemParent
    }

    override fun onBindView(item: ModifyItem, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        item as ModifyItemParent
        notifyExpand(vh, item.isExpand)
        vh.binding.tvState.text = item.getStateText()
        vh.binding.tvTitle.text = item.name
        if (item.children.isNotEmpty()) {
            vh.binding.root.setOnClickListener {
                itemClick.invoke(item, position)
                notifyExpand(vh, item.isExpand)
            }
        } else {
            vh.binding.ivCollapse.visibility = View.INVISIBLE
        }

        if (item.state != ModifyState.APPLIED) {
            vh.binding.tvState.setTextColor(vh.binding.root.context.getColor(R.color.view_debug_dex_state_error))
        } else {
            vh.binding.tvState.setTextColor(vh.binding.root.context.getColor(R.color.view_debug_dex_state_ok))
        }

    }

    private fun notifyExpand(vh: VH, isExpand: Boolean) {
        if (isExpand) {
            vh.binding.ivCollapse.setImageResource(R.mipmap.view_debug_arrow_up)
        } else {
            vh.binding.ivCollapse.setImageResource(R.mipmap.view_debug_arrow_down)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(parent)
    }

    private class VH(
        parent: ViewGroup,
        val binding: ViewDebugItemModifyItemParentBinding = ViewDebugItemModifyItemParentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {


    }
}