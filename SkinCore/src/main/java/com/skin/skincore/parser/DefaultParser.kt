package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.example.skincore.R
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.IAttrCollector

internal class DefaultParser(private val supportAttr: IntArray) : IParser {
    private var keys = supportAttr

    // private var values = supportAttr.values.toTypedArray()
    private lateinit var collectors: List<IAttrCollector<View>>
    override fun onCollectorLoaded(collectors: List<IAttrCollector<View>>) {
        this.collectors = collectors
    }

    override fun parse(view: View, attributeSet: AttributeSet, outValue: ParseOutValue) {
        val attrs = mutableListOf<Attrs>()
        val typedArray = view.context.obtainStyledAttributes(
            attributeSet,
            keys
        )

        typedArray.let {
            keys.forEachIndexed { index, value ->
                if (typedArray.hasValue(index)) {
                    val resId = typedArray.getResourceId(index, 0)
                    // 排除硬编码
                    if (resId != 0) {
                        val attr = Attrs(
                            resId,
                            value,
                            view.context.resources.getResourceTypeName(resId)
                        )
                        attrs.add(attr)
                    }
                }
            }
        }
        typedArray.recycle()
        outValue.attrs = attrs
        outValue.skinAttrValue = getSkinAttrValue(view, attributeSet)
    }

    private val skinAttr = intArrayOf(R.attr.skin)

    /**
     * 判断当前View是否设置app:skin=”“属性
     */
    private fun getSkinAttrValue(view: View, attributeSet: AttributeSet): Int {
        val ta = view.context.obtainStyledAttributes(attributeSet, skinAttr)
        if (ta.hasValue(0)) {
            return if (ta.getBoolean(0, false)) {
                ParseOutValue.SKIN_ATTR_TRUE
            } else {
                ParseOutValue.SKIN_ATTR_FALSE
            }
        }
        ta.recycle()
        return ParseOutValue.SKIN_ATTR_UNDEFINE
    }
}
