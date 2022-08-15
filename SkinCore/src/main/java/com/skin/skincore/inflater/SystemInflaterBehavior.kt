package com.skin.skincore.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

/**
 * 当走到privateFactory时，如果返回仍然为null，最后一部就是调用createView
 */
class SystemInflaterBehavior: LayoutInflater.Factory2 {
    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if ("ViewStub" == name) {
            Class.forName(name)
        }  else if (name.indexOf('.') == -1) {

        }
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        TODO("Not yet implemented")
    }
}