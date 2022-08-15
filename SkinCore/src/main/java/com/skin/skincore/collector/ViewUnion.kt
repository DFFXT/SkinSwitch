package com.skin.skincore.collector

import android.view.View
import java.lang.ref.WeakReference

/**
 * view容器
 */
class ViewUnion(view: View) {
    private val attrs = HashMap<String, Attrs>()
    private val viewRef = WeakReference(view)
    fun addAttr(attr: Attrs) {
        attrs[attr.name] = attr
    }
}