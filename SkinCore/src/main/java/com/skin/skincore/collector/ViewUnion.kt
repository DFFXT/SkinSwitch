package com.skin.skincore.collector

import android.view.View
import com.example.skincore.R

/**
 * view容器
 */
class ViewUnion(attrs: List<Attrs>? = null) : Iterable<Map.Entry<String, Attrs>> {
    private val attrsMap = HashMap<String, Attrs>()

    init {
        attrs?.forEach {
            addAttr(it)
        }
    }

    fun addAttr(attr: Attrs) {
        attrsMap[attr.name] = attr
    }

    fun addAttr(attrs: List<Attrs>) {
        attrs.forEach {
            addAttr(it)
        }
    }

    override fun iterator(): Iterator<Map.Entry<String, Attrs>> = attrsMap.iterator()
}

/**
 * 获取View中的换肤属性
 */
fun View.getViewUnion(): ViewUnion? {
    return (this.getTag(R.id.view_sKinAttr) as? ViewUnion)
}

/**
 * 添加换肤属性
 */
fun View.addViewSkinAttrs(attr: Attrs) {
    var union = this.getViewUnion()
    if (union == null) {
        union = ViewUnion().apply {
            addAttr(attr)
        }
        this.setTag(R.id.view_sKinAttr, union)
    } else {
        union.addAttr(attr)
    }
}

fun View.addViewSkinAttrs(attrs: List<Attrs>) {
    var union = this.getViewUnion()
    if (union == null) {
        union = ViewUnion(attrs)
        this.setTag(R.id.view_sKinAttr, union)
    } else {
        union.addAttr(attrs)
    }
}
