package com.skin.skincore.parser

import android.util.AttributeSet
import android.view.View
import com.example.skincore.R
import com.skin.skincore.collector.Attrs

internal class DefaultParser(private val supportAttr: HashSet<Int>) : IParser {
    // 升序数组
    private var keys: IntArray = supportAttr.toIntArray().sortedArray()

    /**
     * 新增属性
     */
    fun addSupportAttr(attrId: Int) {
        supportAttr.add(attrId)
        keys = supportAttr.toIntArray().sortedArray()
    }

    override fun parse(view: View, attributeSet: AttributeSet, outValue: ParseOutValue) {
        val attrs = mutableListOf<Attrs>()
        // obtainStyledAttributes 参数2必须是升序数组
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
