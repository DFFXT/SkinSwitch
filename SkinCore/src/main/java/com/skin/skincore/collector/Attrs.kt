package com.skin.skincore.collector

import android.content.res.Resources
import com.skin.skincore.collector.ResType.Companion.UNDEFINE

/**
 * @param resId 资源id，皮肤包中获取时需转换为name再通过name获取资源
 * @param attributeId 属性id (android.R.attr.textColor等)
 */
class Attrs(
    val resId: Int,
    val attributeId: Int
) {
    private var type = UNDEFINE

    /**
     * 获取资源类型 资源类型，颜色，图片等
     */
    fun getResourceType(res: Resources): String {
        if (type == UNDEFINE) {
            type = res.getResourceTypeName(resId)
        }
        return type
    }
}
