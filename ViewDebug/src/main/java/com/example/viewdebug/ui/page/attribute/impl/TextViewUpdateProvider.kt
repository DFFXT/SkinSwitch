package com.example.viewdebug.ui.page.attribute.impl

import android.view.View
import android.widget.TextView
import com.example.viewdebug.ui.page.attribute.Read
import com.example.viewdebug.ui.page.attribute.TextUpdate
import com.example.viewdebug.ui.page.attribute.ViewExtraInfoProvider

/**
 * TextView属性更新集合
 */
internal class TextViewUpdateProvider : ViewExtraInfoProvider<TextView>() {
    override val extraInfoProvider: LinkedHashMap<String, Read<TextView>>
        get() = LinkedHashMap<String, Read<TextView>>().apply {
            this["android:textColor"] = TextColorRead()
            this["android:textSize"] = object : Read<TextView> {
                override fun getValue(view: TextView): String {
                    return view.textSize.toString() + "px|" + (view.textSize / view.context.resources.displayMetrics.density) + "dp"
                }

            }
            this["android:text"] = TextUpdate()
        }

    override fun support(view: View): Boolean {
        return view is TextView
    }

}