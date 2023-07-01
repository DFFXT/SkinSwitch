package com.example.viewdebug

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.ViewDebugInfo
import com.example.viewdebug.util.setViewDebugInfo
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.IAsset
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.AttrParseListener
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.provider.ResourceObjectCreator
import com.skin.skincore.provider.ResourcesProviderManager
import java.util.Collections

/**
 * 自动初始化
 * Initializer startup自启动
 */
@Keep
class ViewDebugInitializer : Initializer<ViewDebugInitializer> {
    override fun create(context: Context): ViewDebugInitializer {
        // 替换换肤框架的MergeResource对象
        ResourcesProviderManager.replaceResourceObjectCreator(object : ResourceObjectCreator {
            override fun createResourceObject(
                asset: IAsset,
                default: Resources,
                themeId: Int,
            ): MergeResource {
                return ViewDebugMergeResource(asset, default, themeId)
            }
        })
        // 新增View解析监听
        SkinManager.addViewAttrParseListener(object : AttrParseListener {
            override fun onAttrParsed(
                parent: View?,
                view: View,
                attributeSet: AttributeSet,
                union: ViewUnion,
            ) {
                val res = view.resources
                // 设置调试信息
                if (res is ViewDebugMergeResource) {
                    view.setViewDebugInfo(ViewDebugInfo(res.getLayoutId(attributeSet)))
                }
            }
        })
        ViewDebugManager.init(context.applicationContext as Application)
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}
