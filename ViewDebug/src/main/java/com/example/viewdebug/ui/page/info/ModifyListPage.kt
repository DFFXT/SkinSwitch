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
import com.example.viewdebug.databinding.ViewDebugLayoutModifyPageBinding
import com.example.viewdebug.dex.DexLoadManager
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.launch
import com.example.viewdebug.xml.XmlManager
import com.fxf.debugwindowlibaray.ui.UIPage
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers

/**
 * 当前应用更改列表
 * 显示更改xml
 * 显示替换kotlin文件
 */
class ModifyListPage : UIPage(), ViewDebugResourceManager.OnResourceChanged {
    private lateinit var binding: ViewDebugLayoutModifyPageBinding
    private val items = ArrayList<ModifyItemHandle.ModifyItem>()
    private val adapter = MultiTypeRecyclerAdapter<ModifyItemHandle.ModifyItem>().apply {
        this.registerItemHandler(ModifyItemHandle())
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
        ItemTouchHelper(object :ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val item = items[viewHolder.adapterPosition]
                return if (item.type == "dex") {
                    0
                } else {
                    makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
                }
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = items[viewHolder.adapterPosition].id
                ViewDebugResourceManager.removeInterceptor(id)
                val type = ctx.resources.getResourceTypeName(id)
                if (type != "layout") {
                    // 移除了非布局资源，刷新全局
                    XmlManager.applyGlobalViewByResId(id)
                }
            }

        }).attachToRecyclerView(binding.rvList)
        binding.root.setOnClickListener {
            // 关闭自身
            WindowControlManager.resetToEmptyPage()
        }
        adjustOrientation(binding.rvList)
        ViewDebugResourceManager.addResourceChangeListener(this)
        // 获取数据
        items.clear()
        items.addAll(DexLoadManager.getAppliedDexList().map { ModifyItemHandle.ModifyItem(it, 0, "dex") })
        items.addAll(ViewDebugResourceManager.getAllChangedResource().convertItems())
        adapter.update(items)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewDebugResourceManager.addResourceChangeListener(this)
    }

    private fun Collection<Int>.convertItems(): List<ModifyItemHandle.ModifyItem> {
        return this.map {
            val type = ctx.resources.getResourceTypeName(it)
            ModifyItemHandle.ModifyItem(
                type + "/" + ctx.resources.getResourceEntryName(it),
                it,
                type
            )
        }
    }

    /**
     * 资源变更监听
     */
    override fun onResourceAdd(id: Int) {
        launch(Dispatchers.Main) {
            if (items.indexOfFirst { it.id == id } == -1) {
                Logger.d("ModifyListPage", "onResourceAdd $id")
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