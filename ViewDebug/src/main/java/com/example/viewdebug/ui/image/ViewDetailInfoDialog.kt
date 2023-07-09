// 包名不可改变
package androidx.fragment.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.viewdebug.R
import com.example.viewdebug.databinding.ViewDebugDialogDetailInfoBinding
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.dialog.BaseDialog
import com.example.viewdebug.ui.skin.textColor
import com.example.viewdebug.util.adjustOrientation
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled
import com.example.viewdebug.util.getViewDebugInfo
import java.util.LinkedList

/**
 * view的想象信息
 */
internal class ViewDetailInfoDialog(host: UIPage) : BaseDialog(host) {
    private lateinit var binding: ViewDebugDialogDetailInfoBinding
    override fun onCreateDialog(ctx: Context): View {
        binding = ViewDebugDialogDetailInfoBinding.inflate(LayoutInflater.from(host.tabView.context), host.tabView.parent as ViewGroup, false)
        adjustOrientation(binding.root)
        return binding.root
    }

    fun show(target: View) {
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

        applyReference()
        show()
    }

    private val leftIds = LinkedList<Int>()
    private val rightIds = LinkedList<Int>()

    /**
     * 添加详细信息
     */
    private fun addDescribe(label: String, value: String) {
        val tvLabel = AppCompatTextView(host.tabView.context)
        tvLabel.text = label
        tvLabel.textSize = tvLabel.resources.getDimension(R.dimen.view_debug_common_text_size)
        tvLabel.textColor(R.color.view_debug_black)
        tvLabel.id = View.generateViewId()
        val tvValue = AppCompatTextView(host.tabView.context)
        tvValue.text = value
        tvValue.textSize = tvLabel.resources.getDimension(R.dimen.view_debug_common_text_size)
        tvValue.textColor(R.color.view_debug_black)
        tvValue.id = View.generateViewId()

        binding.layoutContent.addView(tvLabel)
        binding.layoutContent.addView(tvValue)
        leftIds.add(tvLabel.id)
        rightIds.add(tvValue.id)
    }

    private fun applyReference() {
        binding.flowLeft.referencedIds = leftIds.toIntArray()
        binding.flowRight.referencedIds = rightIds.toIntArray()
    }
}