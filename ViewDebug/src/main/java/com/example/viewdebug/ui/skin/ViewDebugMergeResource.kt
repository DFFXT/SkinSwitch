package com.example.viewdebug.ui.skin

import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import com.skin.skincore.asset.IAsset
import com.skin.skincore.provider.MergeResource
import java.util.WeakHashMap

/**
 * view debug专用的resource对象
 */
class ViewDebugMergeResource(asset: IAsset, default: Resources, themeId: Int) :
    MergeResource(asset, default, themeId) {
    private val layoutMap = WeakHashMap<AttributeSet, Int>()

    override fun getLayout(id: Int): XmlResourceParser {
        val parser = super.getLayout(id)
        layoutMap[parser] = id
        return parser
    }

    /**
     * 获取对应的布局id
     */
    fun getLayoutId(attributeSet: AttributeSet): Int? {
        return layoutMap[attributeSet]
    }
}
