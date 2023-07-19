package com.example.viewdebug.ui.image.attribute.impl

import android.view.View
import android.widget.TextView
import com.example.viewdebug.ui.image.attribute.TextUpdate
import com.example.viewdebug.ui.image.attribute.Update
import com.example.viewdebug.ui.image.attribute.ViewUpdateProvider

/**
 * TextView属性更新集合
 */
internal class TextViewUpdateProvider : ViewUpdateProvider<TextView>() {
    override val update: LinkedHashMap<String, Update<TextView>>
        get() = LinkedHashMap<String, Update<TextView>>().apply {
            this["android:text"] = TextUpdate()
        }

    override fun support(view: View): Boolean {
        return view is TextView
    }

}