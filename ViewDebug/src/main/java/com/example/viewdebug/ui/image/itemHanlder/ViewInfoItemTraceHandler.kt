package com.example.viewdebug.ui.image.itemHanlder

import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView

/**
 * trace类型跳转
 */
internal class ViewInfoItemTraceHandler(private val click: (ViewDetailInfoDialog.Item) -> Unit) : ViewInfoItemHandler() {
    override fun handle(item: ViewDetailInfoDialog.Item): Boolean {
        return item.type == ViewDetailInfoDialog.Item.TYPE_TRACE_JUMP
    }

    override fun onBindView(item: ViewDetailInfoDialog.Item, position: Int, vh: RecyclerView.ViewHolder) {
        super.onBindView(item, position, vh)
        vh as VH
        vh.binding.tvName.setOnClickListener {
            click.invoke(item)
        }
    }

}