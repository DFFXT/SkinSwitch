package com.example.viewdebug.ui.page.itemHanlder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.ViewDetailInfoDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.databinding.ViewDebugLayoutImageItemBinding
import com.example.viewdebug.rv.ItemHandle
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.page.ImageDetailDialog
import com.example.viewdebug.util.copyToClipboard
import com.example.viewdebug.util.tryShowXmlText
import com.skin.skincore.SkinManager
import com.skin.skincore.collector.removeSkinAttr
import com.skin.skincore.collector.setImageResourceSkinAble
import java.lang.ref.WeakReference

/**
 * 图片显示适配器
 */
internal class ImageItemHandler(private val host: UIPage, private val itemClick: (View?) -> Unit) : ItemHandle<Item>() {

    private var onAttributeNameClick: ((Item) -> Unit)? = null
    private var onLayoutNameClick: ((Item) -> Unit)? = null
    private var onImageClick: ((Item) -> Unit)? = null
    private var onItemClick: ((Item) -> Unit)? = null

    init {
        this.onImageClick = click@{
            if (it.id <= 0) return@click
            val dialog = ImageDetailDialog(host)
            dialog.show(it.id)
        }
        this.onLayoutNameClick = {
            copyToClipboard(host.tabView.context, it.layoutName)
            if (it.layoutId != 0) {
                tryShowXmlText(host.ctx, it.layoutId, host, null, null)
            }
        }
        this.onAttributeNameClick = {
            if (!tryShowXmlText(host.ctx, it.id, host, it.attributeId, it.target)) {
                // 不显示xml内容，则进入详情页
                this.onItemClick?.invoke(it)
            }
        }
        this.onItemClick = {
            val target = it.target.get()
            if (target != null) {
                val dialog = ViewDetailInfoDialog(host)
                dialog.show(target)
            } else {
                Toast.makeText(host.tabView.context, "对象已经消失", Toast.LENGTH_SHORT).show()
            }
            itemClick(target)
        }
    }

    inner class VH(val binding: ViewDebugLayoutImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Item) {

            try {
                binding.ivImage.setImageResourceSkinAble(item.id)
            } catch (_: Throwable) {
                // 如果使用当前context无法加载对应id，说明这个view使用的资源是其它context的
                binding.ivImage.removeSkinAttr(android.R.attr.src)
                val ctx = item.target.get()?.context ?: binding.ivImage.context
                val provider = SkinManager.getResourceProvider(ctx)
                // 通过对应context的provider来加载
                binding.ivImage.setImageDrawable(provider.getDrawable(item.id, null))
            }
            binding.tvTitle.text = item.layoutName
            binding.tvName.text = item.attribute

            binding.tvName.setOnClickListener {
                onAttributeNameClick?.invoke(item)
            }
            binding.tvTitle.setOnClickListener {
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
}

class Item(
    val target: WeakReference<View>,
    val id: Int,
    val layoutName: String,
    val layoutId: Int,
    val attributeId: Int,
    val attribute: String
)
