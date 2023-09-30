package com.skin.skincore.collector

import android.view.View
import java.util.WeakHashMap

/**
 * 视图收集器
 * 包含了通过该context创建的所有视图
 */
internal class ViewContainer : Iterable<MutableMap.MutableEntry<View, Unit>> {
    // private val viewRef: HashMap<Int, ViewUnion> = hashMapOf()
    private val viewRef = WeakHashMap<View, Unit>()

    fun add(view: View, union: ViewUnion): ViewUnion {
        view.setViewUnion(union)
        viewRef[view] = Unit
        return union
    }

    fun remove(view: View) {
        viewRef.remove(view)
        view.clearSkinAttr()
    }

    operator fun get(view: View): ViewUnion? = view.getViewUnion()

    override fun iterator(): Iterator<MutableMap.MutableEntry<View, Unit>> {
        return viewRef.iterator()
    }
}
