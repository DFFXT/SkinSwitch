package com.example.viewdebug.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * item 处理器
 */
abstract class ItemHandle<T> {
    fun canHandle(item: Any): Boolean {
        val t = item as? T ?: return false
        return handle(t)
    }

    /**
     * 是否处理该item
     */
    protected abstract fun handle(item: T): Boolean

    abstract fun onBindView(item: T, position: Int, vh: RecyclerView.ViewHolder)

    open fun getViewType(): Int = this.hashCode()

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
}
