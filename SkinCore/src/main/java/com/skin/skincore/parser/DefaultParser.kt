package com.skin.skincore.parser

import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import android.view.View
import com.example.skincore.R
import com.skin.skincore.collector.Attrs
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.collector.getViewUnion
import com.skin.skincore.provider.MergeResource

internal class DefaultParser(supportAttr: HashSet<Int>) : IParser {
    private val supportedAttrInternal = supportAttr.toHashSet()

    // 升序数组
    private var keys: IntArray

    init {
        supportedAttrInternal.add(R.attr.skin)
        supportedAttrInternal.add(R.attr.skin_forDescendants)
        keys = supportedAttrInternal.toIntArray().sortedArray()
    }

    /**
     * 新增属性
     */
    fun addSupportAttr(attrId: Int) {
        supportedAttrInternal.add(attrId)
        keys = supportedAttrInternal.toIntArray().sortedArray()
    }

    override fun parse(parent: View?, view: View, attributeSet: AttributeSet): ViewUnion {
        // obtainStyledAttributes 参数2必须是升序数组
        val typedArray = view.context.obtainStyledAttributes(
            attributeSet,
            keys,
        )
        val union = ViewUnion()
        val parentUnion = parent?.getViewUnion()
        typedArray.let {
            keys.forEachIndexed { index, value ->
                if (value == R.attr.skin) {
                    // 解析skin属性
                    union.skinAttrValue = getBooleanState(typedArray, index)
                } else if (value == R.attr.skin_forDescendants) {
                    // 解析skin_inherited属性
                    union.skinForDescendants = getBooleanState(typedArray, index)
                } else if (typedArray.hasValue(index)) {
                    val resId = typedArray.getResourceId(index, 0)
                    if (resId != 0) {
                        val attr = Attrs(
                            resId,
                            value,
                        )
                        union.addAttr(attr)
                    }
                }
            }
        }
        typedArray.recycle()

        if (union.skinForDescendants == ParseOutValue.SKIN_ATTR_TRUE) {
            // 需要将当前值传递给后代
            union.skinInheritedValue = union.skinAttrValue
        } else if (parentUnion != null) {
            // 不需要将当前值传递给后代，子代使用上代数据
            union.skinInheritedValue = parentUnion.skinInheritedValue
        }

        // 如果自己设置了值，则使用自己的
        /*if (union.skinAttrValue == ParseOutValue.SKIN_ATTR_UNDEFINE && parentUnion != null) {
            union.skinInheritedValue = parentUnion.skinInheritedValue
        }*/ /*else if (parentUnion != null) {
            // 如果当前view需要继承上层skin
            union.skinInheritedValue = parentUnion.skinInheritedValue
        }*/
        /*val p = xmlParser.get(attributeSet)
        if (p is MergeResource.XMlParserDelegate) {
            union.layoutId = p.layoutId
        }*/
        return union
    }

    /**
     * 获取boolean状态
     */
    private fun getBooleanState(typedArray: TypedArray, index: Int): Int {
        return if (typedArray.hasValue(index)) {
            if (typedArray.getBoolean(index, false)) {
                ParseOutValue.SKIN_ATTR_TRUE
            } else {
                ParseOutValue.SKIN_ATTR_FALSE
            }
        } else {
            ParseOutValue.SKIN_ATTR_UNDEFINE
        }
    }
}
