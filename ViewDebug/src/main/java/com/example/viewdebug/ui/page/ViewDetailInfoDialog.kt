// 包名不可改变
package androidx.fragment.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.example.viewdebug.databinding.ViewDebugDialogDetailInfoBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.fxf.debugwindowlibaray.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.page.PlaintTextDialog
import com.example.viewdebug.ui.page.attribute.Update
import com.example.viewdebug.ui.page.attribute.impl.ViewUpdateProviderManger
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoInputItemHandler
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoItemHandler
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoItemTraceHandler
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled
import com.example.viewdebug.util.getViewDebugInfo
import java.lang.ref.WeakReference

/**
 * view的想象信息
 */
internal class ViewDetailInfoDialog(host: UIPage) : BaseDialog(host) {
    private lateinit var binding: ViewDebugDialogDetailInfoBinding
    private lateinit var targetRef: WeakReference<View>
    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        binding = ViewDebugDialogDetailInfoBinding.inflate(LayoutInflater.from(host.tabView.context), parent, false)
        adjustOrientation(binding.root)
        binding.rvList.adapter = adapter
        adapter.registerItemHandler(ViewInfoItemHandler())
        adapter.registerItemHandler(
            ViewInfoInputItemHandler().apply {
                this.updateClick = { item, arg ->
                    targetRef.get()?.let { target ->
                        (item.extra as Update<View>).update(target, arg)
                    }
                }
            },
        )
        adapter.registerItemHandler(ViewInfoItemTraceHandler {
            PlaintTextDialog(host).show(it.extra as String)
        })
        val lm = binding.rvList.layoutManager as GridLayoutManager
        lm.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position.rem(2) == 0) {
                    1
                } else {
                    3
                }
            }
        }
        return binding.root
    }

    private val adapter = MultiTypeRecyclerAdapter<Item>()
    private val data = ArrayList<Item>()

    fun show(target: View) {
        this.targetRef = WeakReference(target)
        show()
        addDescribe(Item.TYPE_COMMON,"Target", target::class.java.simpleName)
        // region 添加id信息
        if (target.id > 0) {
            val idName = target.resources.getResourceName(target.id)
            addDescribe(Item.TYPE_COMMON,"id", idName)
        }
        // endregion
        addDescribe(Item.TYPE_COMMON,"Owner(Activity)", target.context::class.java.simpleName)
        // region 添加fragment信息
        ViewTreeLifecycleOwner.get(target)?.let {
            try {
                if (it is FragmentViewLifecycleOwner) {
                    val fragmentName = fragmentViewLifecycleOwnerFragmentFiled.get(it)::class.java.name
                    addDescribe(Item.TYPE_COMMON,"Owner(Fragment)", fragmentName)
                }
            } catch (_: Exception) {
            }
        }
        // endregion
        val debugInfo = target.getViewDebugInfo()
        if (debugInfo != null) {
            addDescribe(Item.TYPE_COMMON,"Owner(layout)", debugInfo.getLayoutTypeAndName(target.resources) ?: "")
            addDescribe(Item.TYPE_TRACE_JUMP,"", "查看布局生成调用栈", debugInfo.getMainInvokeTrace())
        }


        addAttributeUpdate(target)
        adapter.update(data)
    }

    /**
     * 添加详细信息
     */
    private fun addDescribe(type: Int, label: String, value: String, extra: Any? = null) {
        data.add(Item(Item.TYPE_COMMON, label, null))
        data.add(Item(type, value, extra))
    }

    private fun addAttributeUpdate(target: View) {
        val m = ViewUpdateProviderManger()
        val provider = m.getProvider(target)
        provider?.update?.forEach {
            addDescribe(Item.TYPE_UPDATE, it.key, it.value.getValue(target), it.value)
        }
    }

    class Item(val type: Int, val name: String,val extra: Any?) {
        companion object {
            const val TYPE_COMMON = 0
            const val TYPE_UPDATE = 1
            const val TYPE_TRACE_JUMP = 2
        }
    }
}
