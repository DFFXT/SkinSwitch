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

/**
 * 获取当前view所属的fragment
 */
class FragmentInfoRead : Read<View> {
    override fun getValue(view: View): CharSequence? {
        ViewTreeLifecycleOwner.get(view)?.let {
            try {
                if (it is FragmentViewLifecycleOwner) {
                    val showText = fragmentViewLifecycleOwnerFragmentFiled.get(it)::class.java.name
                    val sps = SpannableString(showText)
                    if (ServerManager.isConnected()) {
                        sps.setSpan(ForegroundColorSpan(Color.RED), 0, showText.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    // 当没有连接时，仍然触发点击事件，只是不高亮显示和不显示下划线
                    sps.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            RemoteControl.openClass(showText)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (ServerManager.isConnected()) {
                                super.updateDrawState(ds)
                            }
                        }
                    }, 0, showText.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                    return sps
                }
            } catch (_: Exception) {
            }
        }
        return null
    }
}