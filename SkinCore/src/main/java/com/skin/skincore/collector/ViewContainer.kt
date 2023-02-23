package com.skin.skincore.collector

import android.view.View
import com.skin.skincore.parser.ParseOutValue
import java.util.*

/**
 * 视图收集器
 * 包含了通过该context创建的所有视图
 */
internal class ViewContainer : MutableIterable<MutableMap.MutableEntry<View, Unit>> {
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

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<View, Unit>> {
        return viewRef.iterator()
    }
}
