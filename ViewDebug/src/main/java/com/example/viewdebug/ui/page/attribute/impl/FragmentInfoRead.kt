package androidx.fragment.app

import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.viewdebug.server.RemoteControl
import com.example.viewdebug.server.ServerManager
import com.example.viewdebug.ui.page.attribute.Read
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled
import kotlin.math.sign

/**
 * 获取当前view所属的fragment
 */
class FragmentInfoRead : Read<View> {
    override fun getValue(view: View): CharSequence? {
        var name = getViewFragmentAndActivityName(view)
        name ?: return null
        var nameList = name.split("\n")
        nameList = nameList.mapIndexed { index, s ->  getIndentString(index * 2, s)}
        name = nameList.joinToString("\n")
        val sps = SpannableString(name)
        if (ServerManager.isConnected()) {
            var offset = 0
            nameList.forEachIndexed { index, s ->
                sps.setSpan(ForegroundColorSpan(Color.RED), offset, offset + s.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                // 当没有连接时，仍然触发点击事件，只是不高亮显示和不显示下划线
                sps.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        RemoteControl.openClass(s)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        if (ServerManager.isConnected()) {
                            super.updateDrawState(ds)
                        }
                    }
                }, offset, offset + s.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                offset += s.length
            }
        }
        return sps
    }

    /**
     * 返回的字符串是换行字符串
     */
    private fun getViewFragmentAndActivityName(view: View?) : String? {
        view ?: return null
        ViewTreeLifecycleOwner.get(view)?.let {
            if (it is FragmentViewLifecycleOwner) {
                val fragment = fragmentViewLifecycleOwnerFragmentFiled.get(it) as Fragment
                val showText = fragment::class.java.name
                val parentFragment = getViewFragmentAndActivityName(fragment.view?.parent as? View)
                return if (parentFragment != null) {
                    parentFragment + "\n" + showText
                } else {
                    showText
                }
            }
        }
        // 没有fragment了直接返回activity的名称
        return view.context::class.java.simpleName
    }

    /**
     * 前面新增lines个空格
     */
    private fun getIndentString(spaceCount: Int, s: String): String {
        var returnStr = ""
        repeat(spaceCount) {
            returnStr += " "
        }
        return returnStr + s
    }
}