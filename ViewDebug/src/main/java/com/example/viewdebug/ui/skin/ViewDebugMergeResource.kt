package com.example.viewdebug.ui.skin

import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.writer.helper.value.AttrColorValueCompile
import com.skin.skincore.asset.IAsset
import com.skin.skincore.provider.MergeResource
import java.util.WeakHashMap

/**
 * view debug专用的resource对象
 */
class ViewDebugMergeResource(asset: IAsset, default: Resources, themeIds: IntArray) :
    MergeResource(asset, default, themeIds) {
    private val layoutMap = WeakHashMap<AttributeSet, LayoutInfo>()

    override fun getLayout(id: Int): XmlResourceParser {
        val parser = if (ViewDebugResourceManager.getAllChangedResource().contains(id)) {
            val p = ViewDebugResourceManager.interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.FOLDER}/$id.xml")
            p
        } else {
            super.getLayout(id)
        }
        layoutMap[parser] = LayoutInfo(id, Throwable().stackTrace)

        return parser
    }

    /**
     * 拦截布局
     */
    override fun getXml(id: Int): XmlResourceParser {
        if (ViewDebugResourceManager.getAllChangedResource().contains(id)) {
            return ViewDebugResourceManager.interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.FOLDER}/$id.xml")
        }
        return super.getXml(id)
    }

    /**
     * 拦截drawable
     */
    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        if (ViewDebugResourceManager.getAllChangedResource().contains(id)) {
            val parser = ViewDebugResourceManager.interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.FOLDER}/$id.xml")
            return Drawable.createFromXml(this, parser, theme)
        }
        return super.getDrawableForDensity(id, density, theme)
    }

    override fun getColor(id: Int, theme: Theme?): Int {
        val color = ViewDebugResourceManager.getAllValueChangedItem()[id]
        if (color != null) {
            return ResourceDecode.getColor(this, color, theme) ?: super.getColor(id, theme)
        }
        return super.getColor(id, theme)
    }


    /**
     * 拦截颜色
     */
    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
        if (ViewDebugResourceManager.getAllChangedResource().contains(id)) {
            val parser = ViewDebugResourceManager.interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.FOLDER}/$id.xml")
            return ColorStateList.createFromXml(this, parser, theme)
        }
        return super.getColorStateList(id, theme)
    }


    /**
     * 获取对应的布局id
     */
    fun getLayoutInfo(attributeSet: AttributeSet): LayoutInfo? {
        return layoutMap[attributeSet]
    }

    class LayoutInfo(val layoutId: Int, val invokeTrace: Array<StackTraceElement>)


}
