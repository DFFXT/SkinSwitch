package com.example.viewdebug.ui.page.info

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.R
import com.example.viewdebug.apply.ModifyState
import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.example.viewdebug.databinding.ViewDebugLayoutModifyPageBinding
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.relaunchApp
import com.example.viewdebug.util.shortToast
import com.fxf.debugwindowlibaray.ui.UIPage
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import java.util.regex.Pattern

/**
 * 当前应用更改列表
 * 显示更改xml
 * 显示替换kotlin文件
 */
class ModifyListPage : UIPage(), ViewDebugResourceManager.OnResourceChanged {
    private lateinit var binding: ViewDebugLayoutModifyPageBinding
    private val items = ArrayList<ModifyItem>()
    private val adapter = MultiTypeRecyclerAdapter<ModifyItem>().apply {
        this.registerItemHandler(ModifyItemChildHandle())
        this.registerItemHandler(ModifyItemParentHandle { item, index ->
            if (item.isExpand) {
                item.isExpand = false
               items.removeAll(item.children.toSet())
                notifyItemRangeRemoved(index + 1, item.children.size)
            } else {
                item.isExpand = true
                items.addAll(index + 1, item.children)
                notifyItemRangeInserted(index + 1, item.children.size)
            }
        })
    }

    override fun getTabIcon(): Int = R.mipmap.view_debug_modify_list

    override fun onCreateTabView(ctx: Context, parent: ViewGroup): View {
        return super.onCreateTabView(ctx, parent).apply {
            this as ImageView
            imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        binding = ViewDebugLayoutModifyPageBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.rvList.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val item = items[viewHolder.adapterPosition]
                return if (item is ModifyItemParent || item.type == RemoteFileReceiver.FileWatcher.TYPE_VALUES_XML) {
                    makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
                } else {
                    0
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = items[viewHolder.adapterPosition]
                if (item.type == "dex" && item is ModifyItemParent) {
                    // 移除dex
                    if (item.state == ModifyState.APPLIED || item.state == ModifyState.REBOOT_UPDATABLE) {
                        viewHolder.itemView.context.getString(R.string.view_debug_dex_remove_tip).shortToast()
                    }
                    DexLoadManager.removeAppliedDexList(item.name)
                    items.removeAt(viewHolder.adapterPosition)
                    if (items.removeAll(item.children.toSet())) {
                        adapter.notifyItemRangeRemoved(viewHolder.adapterPosition, 1 + item.children.size)
                    } else {
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                    if (items.isEmpty()) {
                        WindowControlManager.removePage(this@ModifyListPage)
                        WindowControlManager.resetToEmptyPage()
                    }
                } else {
                    if (item.type == RemoteFileReceiver.FileWatcher.TYPE_VALUES_XML) {
                        if (item is ModifyItemParent) {
                            ViewDebugResourceManager.removeAllValues()
                        } else {
                            ViewDebugResourceManager.removeValue(item.id)
                            item as ModifyItemChild
                            item.parent.children.remove(item)
                            items.remove(item)
                            adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        }
                    } else {
                        ViewDebugResourceManager.removeInterceptor(item.id)
                        if (item.type != RemoteFileReceiver.FileWatcher.TYPE_LAYOUT) {
                            // 移除了非布局资源，刷新全局
                            XmlLoadManager.applyGlobalViewByResId(item.id)
                        }
                    }

                }

            }

        }).attachToRecyclerView(binding.rvList)
        binding.root.setOnClickListener {
            // 关闭自身
            WindowControlManager.resetToEmptyPage()
        }
        binding.tvClearAndRestart.setOnClickListener {
            ViewDebugResourceManager.removeAllValues()
            ViewDebugResourceManager.getAllChangedResource().forEach {
                ViewDebugResourceManager.removeInterceptor(it)
            }
            DexLoadManager.clear()
            launch(Dispatchers.IO) {
                relaunchApp(it.context, true)
            }
        }
        adjustOrientation(binding.rvList)
        ViewDebugResourceManager.addResourceChangeListener(this)

        refresh()
        return binding.root
    }


    /**
     * 更新数据
     */
    fun refresh() {
        items.clear()
        val dexItems = ArrayList<ModifyItem>()
        DexLoadManager.getAllDexList().forEach {
            val parent = ModifyItemParent(false, it.key, 0, RemoteFileReceiver.FileWatcher.TYPE_DEX, it.value.getModifyState())
            val children = it.value.classList.map {
                ModifyItemChild(parent, it.key, 0, "class", it.value)
            }.sortedWith { o1, o2 ->
                // 子节点排序
                val p1 = (o1.state.ordinal shl 10) + 1024 - o1.name.length
                val p2 = (o2.state.ordinal shl 10) + 1024 - o2.name.length
                p2 - p1
            }
            parent.children.addAll(children)
            dexItems.add(parent)
        }
        items.addAll(dexItems)
        items.addAll(ViewDebugResourceManager.getAllChangedResource().convertItems())
        val values = ViewDebugResourceManager.getAllValueChangedItem()
        if (values.isNotEmpty()) {
            val valuesParent = ModifyItemParent(false, "values", 0, RemoteFileReceiver.FileWatcher.TYPE_VALUES_XML, ModifyState.APPLIED)
            items.add(valuesParent)
            val children = values.map {
                val name = ctx.resources.getResourceTypeName(it.key) + "/" + ctx.resources.getResourceEntryName(it.key) + "=" + it.value
                ModifyItemChild(valuesParent, name, it.key, RemoteFileReceiver.FileWatcher.TYPE_VALUES_XML, ModifyState.APPLIED)
            }
            valuesParent.children.addAll(children)
        }
        if (items.isEmpty()) {
            WindowControlManager.removePage(this@ModifyListPage)
        } else {
            adapter.update(items)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewDebugResourceManager.addResourceChangeListener(this)
    }

    private fun Collection<Int>.convertItems(): List<ModifyItem> {
        return this.map {
            val type = ctx.resources.getResourceTypeName(it)
            ModifyItemParent(
                false,
                type + "/" + ctx.resources.getResourceEntryName(it),
                it,
                type,
                ModifyState.APPLIED
            )
        }
    }

    /**
     * 资源变更监听
     */
    override fun onResourceAdd(id: Int) {
        launch(Dispatchers.Main) {
            if (items.indexOfFirst { it.id == id } == -1) {
                Logger.d("ModifyListPage", "onResourceAdddddddddddddddddddddddddd $id")
                items.addAll(listOf(id).convertItems())
                adapter.notifyItemInserted(items.size)

            }
        }

    }

    override fun onResourceRemove(id: Int) {
        launch(Dispatchers.Main) {
            val index = items.indexOfFirst { it.id == id }
            if (index != -1) {
                items.removeAt(index)
                adapter.notifyItemRemoved(index)
            }
        }

    }
}