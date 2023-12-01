package androidx.fragment.app

import android.view.View
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.viewdebug.ui.page.attribute.Read
import com.example.viewdebug.util.fragmentViewLifecycleOwnerFragmentFiled

/**
 * 获取当前view所属的fragment
 */
class FragmentInfoRead : Read<View> {
    override fun getValue(view: View): String? {
        ViewTreeLifecycleOwner.get(view)?.let {
            try {
                if (it is FragmentViewLifecycleOwner) {
                    return fragmentViewLifecycleOwnerFragmentFiled.get(it)::class.java.name
                }
            } catch (_: Exception) {
            }
        }
        return null
    }
}