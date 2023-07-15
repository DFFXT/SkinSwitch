package com.example.viewdebug.ui.skin

import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.image.XmlParser
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.skin.skincore.asset.Asset
import com.skin.skincore.asset.IAsset
import com.skin.skincore.provider.MergeResource
import java.io.File
import java.util.WeakHashMap

/**
 * view debug专用的resource对象
 */
class ViewDebugMergeResource(asset: IAsset, default: Resources, themeId: Int) :
    MergeResource(asset, default, themeId) {
    private val layoutMap = WeakHashMap<AttributeSet, Int>()

    override fun getLayout(id: Int): XmlResourceParser {
        val parser = if (layoutInterceptorMapper.contains(id)) {
            val p = interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.TYPE_LAYOUT}/$id.xml")
            p
        } else {
            super.getLayout(id)
        }
        layoutMap[parser] = id

        return parser
    }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        if (drawableInterceptorMapper.contains(id)) {
            val parser = interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.TYPE_LAYOUT}/$id.xml")
            return Drawable.createFromXml(this, parser)
        }
        return super.getDrawableForDensity(id, density, theme)
    }

    /**
     * 获取对应的布局id
     */
    fun getLayoutId(attributeSet: AttributeSet): Int? {
        return layoutMap[attributeSet]
    }

    companion object {
        val layoutInterceptorMapper = HashSet<Int>()
        private val drawableInterceptorMapper = HashSet<Int>()
        var interceptedAsset: AssetManager? = null

        fun addInterceptor(type: String, value: Int) {
            if (type == "layout") {
                layoutInterceptorMapper.add(value)
            } else if (type =="drawable") {
                drawableInterceptorMapper.add(value)
            }
        }
    }
}
