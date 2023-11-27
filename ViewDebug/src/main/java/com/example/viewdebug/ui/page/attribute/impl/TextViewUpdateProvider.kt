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
            this["android:text"] = TextUpdate()
            this["android:textColor"] = TextColorRead()
        }

    override fun support(view: View): Boolean {
        return view is TextView
    }

}