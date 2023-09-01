package com.example.viewdebug.ui.image.itemHanlder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugViewItemInfoBinding
import com.example.viewdebug.databinding.ViewDebugViewItemInputBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.util.copyToClipboard

internal open class ViewInfoItemHandler : ItemHandle<ViewDetailInfoDialog.Item>() {
    override fun handle(item: ViewDetailInfoDialog.Item): Boolean {
        return item.type == ViewDetailInfoDialog.Item.TYPE_COMMON
    }

    override fun onBindView(item: ViewDetailInfoDialog.Item, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.binding.tvName.text = item.name
        if (position.rem(2) == 0) {
            vh.binding.tvName.gravity = Gravity.END or Gravity.TOP
            vh.binding.tvName.setOnClickListener(null)
        } else {
            vh.binding.tvName.gravity = Gravity.START or Gravity.TOP
            vh.binding.tvName.setOnClickListener {
                copyToClipboard(it.context, item.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(
            ViewDebugViewItemInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun getViewType(): Int {
        return R.layout.view_debug_view_item_info
    }

    protected class VH(val binding: ViewDebugViewItemInfoBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}
