package com.example.viewdebug.ui.image

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugLayoutImageItemBinding
import com.example.viewdebug.rv.ItemHandle
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.skin.imageResource
import com.example.viewdebug.util.ViewDebugInfo
import com.example.viewdebug.util.copyToClipboard
import com.example.viewdebug.util.tryShowXmlText
import com.skin.skincore.collector.ViewUnion
import java.lang.ref.WeakReference

/**
 * 图片显示适配器
 */
internal class ImageItemHandler(private val host: UIPage) : ItemHandle<ImageItemHandler.Item>() {

    var onAttributeNameClick: ((Item) -> Unit)? = null
    var onLayoutNameClick: ((Item) -> Unit)? = null
    var onImageClick: ((Item) -> Unit)? = null
    var onItemClick: ((Item) -> Unit)? = null

    init {
        this.onImageClick = {
            val dialog = ImageDetailDialog(host)
            dialog.show(it.id)
        }
        this.onLayoutNameClick = {
            copyToClipboard(host.tabView.context, it.layoutName)
            tryShowXmlText(host.ctx, it.layoutId, host)
        }
        this.onAttributeNameClick = {
            tryShowXmlText(host.ctx, it.id, host)
        }
        this.onItemClick = {
            val target = it.target.get()
            if (target != null) {
                val dialog = ViewDetailInfoDialog(host)
                dialog.show(target)
            } else {
                Toast.makeText(host.tabView.context, "对象已经消失", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            binding.ivImage.setOnClickListener {
                onImageClick?.invoke(item)
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun handle(item: Item): Boolean {
        return item is Item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VH(
            ViewDebugLayoutImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindView(item: Item, position: Int, vh: RecyclerView.ViewHolder) {
        vh as VH
        vh.bind(item)
    }

    class Item(
        val target: WeakReference<View>,
        val id: Int,
        val layoutId: Int,
        val layoutName: String,
        val name: String,
    )

    class ViewItem(
        val target: WeakReference<View>,
        val union: ViewUnion?,
        val debugInf0: ViewDebugInfo?,
    )
}
