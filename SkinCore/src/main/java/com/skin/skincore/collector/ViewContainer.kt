package com.skin.skincore.collector

import android.view.View
import java.util.WeakHashMap

class ViewContainer : MutableIterable<MutableMap.MutableEntry<View, Unit>> {
    // private val viewRef: HashMap<Int, ViewUnion> = hashMapOf()
    private val viewRef = WeakHashMap<View, Unit>()

    fun add(view: View, union: List<Attrs>) {
        view.addViewSkinAttrs(union)
        viewRef[view] = Unit
    }

    operator fun get(view: View): ViewUnion? = view.getViewUnion()

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<View, Unit>> {
        return viewRef.iterator()
    }
}
