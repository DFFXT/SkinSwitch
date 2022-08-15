package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.DefaultCollector
import com.skin.skincore.collector.IAttrCollector

internal class DefaultParser(private val supportAttr: LinkedHashMap<Int, String>) : IParser {
    private var keys = supportAttr.keys.toIntArray()
    private var values = supportAttr.values.toTypedArray()
    private lateinit var collectors: List<IAttrCollector<View>>
    override fun onCollectorLoaded(collectors: List<IAttrCollector<View>>) {
        this.collectors = collectors
    }

    override fun parse(view: View, attributeSet: AttributeSet): List<Attrs> {
        val attrs = mutableListOf<Attrs>()
        val typedArray = view.context.obtainStyledAttributes(
            attributeSet,
            keys
        )
        typedArray.let {
            keys.forEachIndexed { index, _ ->
                if (typedArray.hasValue(index)) {
                    val resId = typedArray.getResourceId(index, 0)
                    val attr = Attrs(resId, values[index])
                    attr.resType = view.context.resources.getResourceTypeName(resId)
                    // color不能解析为drawable
                    if (attr.name == DefaultCollector.ATTR_TEXT_COLOR && attr.resType == Attrs.Drawable) {
                        attr.resType = Attrs.STATE_COLOR
                    }
                    attrs.add(attr)
                }
            }
        }
        typedArray.recycle()
        return attrs
    }
}
