package com.example.viewdebug.ui.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugLayoutImageItemBinding
import com.example.viewdebug.ui.skin.imageResource

/**
 * 图片显示适配器
 */
internal class ImageAdapter : BaseRecyclerAdapter<ImageAdapter.Item, ImageAdapter.VH>() {

    class VH(val binding: ViewDebugLayoutImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.ivImage.imageResource(item.id)
            binding.tvName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ViewDebugLayoutImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(item: Item, holder: VH, position: Int) {
        holder.bind(item)
        holder.binding.tvName.setOnClickListener {

        }
    }

    class Item(val id: Int, val name: String)
}
