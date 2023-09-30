package com.example.viewdebug.ui.page.itemHanlder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugViewItemInputBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.util.copyToClipboard

internal class ViewInfoInputItemHandler : ItemHandle<ViewDetailInfoDialog.Item>() {
    var updateClick: ((ViewDetailInfoDialog.Item, String) -> Unit)? = null
    override fun handle(item: ViewDetailInfoDialog.Item): Boolean {
        return item.type == ViewDetailInfoDialog.Item.TYPE_UPDATE
    }

    override fun onBindView(item: ViewDetailInfoDialog.Item, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.binding.tvName.setText(item.name)
        vh.binding.tvName.setOnClickListener {
            copyToClipboard(it.context, item.name)
        }
        vh.binding.ivSubmit.setOnClickListener {
            updateClick?.invoke(item, vh.binding.tvName.text.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(
            ViewDebugViewItemInputBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun getViewType(): Int {
        return R.layout.view_debug_view_item_input
    }

    private class VH(val binding: ViewDebugViewItemInputBinding) : RecyclerView.ViewHolder(binding.root)
}
