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
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.image.attribute.Update
import com.example.viewdebug.ui.image.attribute.impl.ViewUpdateProviderManger
import com.example.viewdebug.ui.image.itemHanlder.ViewInfoInputItemHandler
import com.example.viewdebug.ui.image.itemHanlder.ViewInfoItemHandler
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
    override fun onCreateDialog(ctx: Context): View {
        binding = ViewDebugDialogDetailInfoBinding.inflate(LayoutInflater.from(host.tabView.context), host.tabView.parent as ViewGroup, false)
        adjustOrientation(binding.root)
        binding.rvList.adapter = adapter
        adapter.registerItemHandler(ViewInfoItemHandler())
        adapter.registerItemHandler(
            ViewInfoInputItemHandler().apply {
                this.updateClick = { item, arg ->
                    targetRef.get()?.let { target ->
                        item.update?.update(target, arg)
                    }
                }
            },
        )
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
        addDescribe("Target", target::class.java.simpleName)
        // region 添加id信息
        if (target.id > 0) {
            val idName = target.resources.getResourceName(target.id)
            addDescribe("id", idName)
        }
        // endregion
        addDescribe("Owner(Activity)", target.context::class.java.simpleName)
        // region 添加fragment信息
        ViewTreeLifecycleOwner.get(target)?.let {
            try {
                if (it is FragmentViewLifecycleOwner) {
                    val fragmentName = fragmentViewLifecycleOwnerFragmentFiled.get(it)::class.java.name
                    addDescribe("Owner(Fragment)", fragmentName)
                }
            } catch (_: Exception) {
            }
        }
        // endregion
        target.getViewDebugInfo()?.getLayoutTypeAndName(target.resources)?.let {
            addDescribe("Owner(layout)", it)
        }

        addAttributeUpdate(target)
        adapter.update(data)
    }

    /**
     * 添加详细信息
     */
    private fun addDescribe(label: String, value: String, update: Update<View>? = null) {
        data.add(Item(label, null))
        data.add(Item(value, update))
    }

    private fun addAttributeUpdate(target: View) {
        val m = ViewUpdateProviderManger()
        val provider = m.getProvider(target)
        provider?.update?.forEach {
            addDescribe(it.key, it.value.getValue(target), it.value)
        }
    }

    class Item(val name: String, val update: Update<View>?)
}
