package com.example.viewdebug.ui.page.attribute.impl

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.ui.page.attribute.Read

/**
 * 获取view所属adapter，方便逻辑代码定位
 */
class RecyclerAdapterReader:Read<View> {
    override fun getValue(view: View): String? {
        return getAdapterName(view)
    }

    /**
     * 向上查看，看这个view是在哪个adapter中
     */
    private fun getAdapterName(view: View): String? {
        if (view is RecyclerView) {
            val adapter = view.adapter
            if (adapter != null) {
                return adapter::class.java.name
            }
            return null
        } else if (view.parent is View) {
            return getAdapterName(view.parent as View)
        }
        return null
    }
}