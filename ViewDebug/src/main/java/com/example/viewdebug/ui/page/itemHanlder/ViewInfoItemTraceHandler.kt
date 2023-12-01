package com.example.viewdebug.ui.page.itemHanlder

import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.ui.page.attribute.Link
import com.fxf.debugwindowlibaray.ui.UIPage

/**
 * trace类型跳转
 */
internal class ViewInfoItemTraceHandler(private val host: UIPage) : ViewInfoItemHandler() {
    override fun handle(item: ViewDetailInfoDialog.Item): Boolean {
        return item.read is Link
    }

    override fun onBindView(item: ViewDetailInfoDialog.Item, position: Int, vh: RecyclerView.ViewHolder) {
        super.onBindView(item, position, vh)
        vh as VH
        vh.binding.tvName.setOnClickListener {
            (item.read as Link).onClick(host)
        }
    }

}