package com.example.viewdebug.ui.image

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    lateinit var data: List<T>

    override fun getItemCount(): Int {
        return if (this::data.isInitialized) {
            data.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(data[position], holder, position)
    }

    abstract fun onBindViewHolder(item: T, holder: VH, position: Int)

    fun update(data: List<T>) {
        this.data = ArrayList(data)
        notifyDataSetChanged()
    }
}
