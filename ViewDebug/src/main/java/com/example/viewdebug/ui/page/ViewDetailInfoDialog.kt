// 包名不可改变
package androidx.fragment.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.example.viewdebug.databinding.ViewDebugDialogDetailInfoBinding
import com.example.viewdebug.rv.MultiTypeRecyclerAdapter
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.page.PlaintTextDialog
import com.example.viewdebug.ui.page.attribute.Read
import com.example.viewdebug.ui.page.attribute.Update
import com.example.viewdebug.ui.page.attribute.ViewInfoProviderManger
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoInputItemHandler
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoItemHandler
import com.example.viewdebug.ui.page.itemHanlder.ViewInfoItemTraceHandler
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled
import com.example.viewdebug.util.getViewDebugInfo
import com.fxf.debugwindowlibaray.ui.UIPage
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
        adapter.registerItemHandler(ViewInfoItemTraceHandler(host))
        adapter.registerItemHandler(
            ViewInfoInputItemHandler().apply {
                this.updateClick = { item, arg ->
                    targetRef.get()?.let { target ->
                        (item.read as Update<View>).update(target, arg)
                    }
                }
            },
        )
        adapter.registerItemHandler(ViewInfoItemHandler())
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
        addAttributeUpdate(target)
        adapter.update(data)
    }

    /**
     * 添加详细信息
     */
    private fun addDescribe(label: String, value: String, extra: Read<*>? = null) {
        data.add(Item(label, null))
        data.add(Item(value, extra))
    }

    private fun addAttributeUpdate(target: View) {
        val provider = ViewInfoProviderManger.getExtraInfo(target)
        provider.forEach {
            addDescribe(it.label, it.value, it.extra)
        }
    }

    class Item(val name: String,val read: Read<*>?)
}
