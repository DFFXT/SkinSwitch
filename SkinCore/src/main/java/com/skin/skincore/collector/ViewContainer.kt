package com.skin.skincore.collector

import android.view.View

class ViewContainer {
    private val viewRef: HashMap<Int, ViewUnion> = hashMapOf()

    fun add(view: View, vararg union: Attrs) {
        viewRef[view.hashCode()] = ViewUnion(view).apply {
            union.forEach {
                this.addAttr(it)
            }
        }
    }

    operator fun get(view: View): ViewUnion? = viewRef[view.hashCode()]
}
