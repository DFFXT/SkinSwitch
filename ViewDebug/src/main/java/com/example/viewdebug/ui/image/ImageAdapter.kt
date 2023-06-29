package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugLayoutImageItemBinding
import com.example.viewdebug.ui.skin.imageResource

/**
 * 图片显示适配器
 */
internal class ImageAdapter : BaseRecyclerAdapter<ImageAdapter.Item, ImageAdapter.VH>() {

    var onAttributeNameClick: ((Item) -> Unit)? = null
    var onLayoutNameClick: ((Item) -> Unit)? = null

    inner class VH(val binding: ViewDebugLayoutImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Item) {
            binding.ivImage.imageResource(item.id)
            binding.tvLayout.text = item.layoutName
            binding.tvName.text =
                item.name + ":@" + itemView.context.resources.getResourceTypeName(item.id) +
                "/" + itemView.context.resources.getResourceEntryName(item.id)
            binding.tvName.setOnClickListener {
                onAttributeNameClick?.invoke(item)
            }
            binding.tvLayout.setOnClickListener {
                onLayoutNameClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ViewDebugLayoutImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(item: Item, holder: VH, position: Int) {
        holder.bind(item)
    }

    class Item(
        val id: Int,
        val layoutName: String,
        val name: String,
    )
}
