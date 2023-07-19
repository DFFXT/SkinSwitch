// 包名不可改变
package androidx.fragment.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugDialogDetailInfoBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.image.attribute.Update
import com.example.viewdebug.ui.image.attribute.impl.ViewUpdateProviderManger
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled
import com.example.viewdebug.util.getViewDebugInfo
import com.example.viewdebug.util.setSize
import com.google.android.material.textview.MaterialTextView
import com.skin.skincore.collector.setBackgroundResourceSkinAble
import com.skin.skincore.collector.setTextColorSkinAble
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * view的想象信息
 */
internal class ViewDetailInfoDialog(host: UIPage) : BaseDialog(host) {
    private lateinit var binding: ViewDebugDialogDetailInfoBinding
    private lateinit var targetRef:WeakReference<View>
    override fun onCreateDialog(ctx: Context): View {
        binding = ViewDebugDialogDetailInfoBinding.inflate(LayoutInflater.from(host.tabView.context), host.tabView.parent as ViewGroup, false)
        adjustOrientation(binding.root)
        return binding.root
    }

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
        applyReference()
    }

    private val leftIds = LinkedList<Int>()
    private val rightIds = LinkedList<Int>()

    /**
     * 添加详细信息
     */
    private fun addDescribe(label: String, value: String, update: Update<View>? = null) {
        val tvLabel = AppCompatTextView(host.tabView.context)
        tvLabel.text = label
        tvLabel.textSize = tvLabel.resources.getDimension(R.dimen.view_debug_common_text_size)
        tvLabel.setTextColorSkinAble(R.color.view_debug_black)
        tvLabel.id = View.generateViewId()


        val tvValue = EditText(host.tabView.context)

        tvValue.minWidth = 100
        tvValue.setPadding(0)
        tvValue.setText(value)
        tvValue.textSize = tvLabel.resources.getDimension(R.dimen.view_debug_common_text_size)
        tvValue.setTextColorSkinAble(R.color.view_debug_black)
        tvValue.id = View.generateViewId()

        binding.layoutContent.addView(tvLabel)
        binding.layoutContent.addView(tvValue)
        leftIds.add(tvLabel.id)
        rightIds.add(tvValue.id)

        if (update == null) {
            tvValue.isClickable = false
            tvValue.isFocusable = false
            tvValue.background = null
        } else {
            tvValue.setBackgroundResourceSkinAble(R.drawable.view_debug_bottom_line_1dp)
            addUpdateView(tvValue, update)
        }

    }

    private fun addUpdateView(anchor: TextView, update: Update<View>) {
        val updateView = View(host.ctx)
        updateView.layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
            this.startToEnd = anchor.id
            this.bottomToBottom = anchor.id
        }
        updateView.setSize(host.ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_control_ui_status_bar_height), host.ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_control_ui_status_bar_height))
        updateView.setBackgroundResourceSkinAble(R.mipmap.view_debug_icon_attribute_update)
        updateView.setOnClickListener {
            targetRef.get()?.let {
                update.update(it, anchor.text.toString())
            }
        }
        binding.layoutContent.addView(updateView)
    }

    private fun addAttributeUpdate(target: View) {
        val m = ViewUpdateProviderManger()
        val provider = m.getProvider(target)
        provider?.update?.forEach {
            addDescribe(it.key, it.value.getValue(target), it.value)
        }
    }

    private fun applyReference() {
        binding.flowLeft.referencedIds = leftIds.toIntArray()
        binding.flowRight.referencedIds = rightIds.toIntArray()
    }
}
