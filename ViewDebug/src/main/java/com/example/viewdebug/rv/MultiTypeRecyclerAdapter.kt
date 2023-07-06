package com.example.viewdebug.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 多item类型的RecyclerViewAdapter
 */
class MultiTypeRecyclerAdapter<T : Any> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemHandler: ArrayList<ItemHandle<T>> = ArrayList()

    lateinit var data: List<T>

    /**
     * 注册处理器
     */
    fun registerItemHandler(itemHandle: ItemHandle<*>) {
        itemHandler.add(itemHandle as ItemHandle<T>)
    }

    override fun getItemCount(): Int {
        return if (this::data.isInitialized) {
            data.size
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        for (handler in itemHandler) {
            if (handler.getViewType() == viewType) {
                return handler.onCreateViewHolder(parent, viewType)
            }
        }
        throw Exception("onCreateViewHolder error, no viewType: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        for (handler in itemHandler) {
            if (handler.canHandle(data[position])) {
                handler.onBindView(data[position], position, holder)
                break
            }
        }
        throw Exception("onBindViewHolder error, no handler for: $position")
    }

    override fun getItemViewType(position: Int): Int {
        for (handler in itemHandler) {
            if (handler.canHandle(data[position])) {
                return handler.getViewType()
            }
        }
        throw Exception("getItemViewType error, no viewType for: $position")
    }

    fun update(data: List<T>) {
        this.data = ArrayList(data)
        notifyDataSetChanged()
    }
}
