package com.example.viewdebug.ui.skin

import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.skin.log.Logger
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.IAsset
import com.skin.skincore.plug.SpeedUpSwitchSkin
import com.skin.skincore.provider.MergeResource
import java.util.WeakHashMap

/**
 * view debug专用的resource对象
 */
class ViewDebugMergeResource(asset: IAsset, default: Resources, themeIds: IntArray) :
    MergeResource(asset, default, themeIds) {
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

    override fun getXml(id: Int): XmlResourceParser {
        if (layoutInterceptorMapper.contains(id) || drawableInterceptorMapper.contains(id)) {
            return interceptedAsset!!.openXmlResourceParser("assets/${PackAssetsFile.TYPE_LAYOUT}/$id.xml")
        }
        return super.getXml(id)
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
            } else if (type == "drawable") {
                drawableInterceptorMapper.add(value)
                // 如果没有开启创建即换肤，则开启，否则不生效，需要触发换肤才生效
                if (!SkinManager.isApplyWhenCreate()) {
                    Logger.e("ViewDebugMergeResource", "applyWhenCreate has not open, now auto open")
                    SkinManager.applyWhenCreate(true)

                }
                // 不允许拦截刚创建的view
                if (SpeedUpSwitchSkin.canInterceptOnCreatedView) {
                    Logger.e("ViewDebugMergeResource", "canInterceptOnCreatedView has open, now close")
                    SpeedUpSwitchSkin.canInterceptOnCreatedView = false
                }
            }
        }
    }
}
