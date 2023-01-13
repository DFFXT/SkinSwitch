package com.skin.skincore.collector

import android.view.View
import java.util.WeakHashMap

/**
 * 视图收集器
 * 包含了通过该context创建的所有视图
 */
class ViewContainer : MutableIterable<MutableMap.MutableEntry<View, Unit>> {
    // private val viewRef: HashMap<Int, ViewUnion> = hashMapOf()
    private val viewRef = WeakHashMap<View, Unit>()

    fun add(view: View, union: List<Attrs>) {
        view.addViewSkinAttrs(union)
        viewRef[view] = Unit
    }

    fun remove(view: View) {
        viewRef.remove(view)
        view.clearSkinAttr()
    }

    operator fun get(view: View): ViewUnion? = view.getViewUnion()

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<View, Unit>> {
        return viewRef.iterator()
    }
}
