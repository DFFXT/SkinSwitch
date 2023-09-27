package com.example.viewdebug

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.example.viewdebug.apply.ChangeApplyManager
import com.example.viewdebug.apply.IBuildIdentification
import com.example.viewdebug.apply.dex.ClassLoadObserve
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.ViewDebugInfo
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.setViewDebugInfo
import com.example.viewdebug.xml.AndroidXmRuleManager
import com.skin.skincore.SkinManager
import com.skin.skincore.asset.IAsset
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.AttrParseListener
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.provider.ResourceObjectCreator
import com.skin.skincore.provider.ResourcesProviderManager
import kotlinx.coroutines.Dispatchers
import java.util.Collections

/**
 * 自动初始化
 * Initializer startup自启动
 */
@Keep
open class ViewDebugInitializer : Initializer<ViewDebugInitializer> {
    override fun create(context: Context): ViewDebugInitializer {
        ctx = context.applicationContext as Application
        if (!Settings.canDrawOverlays(context)) {
            // 没有显示浮窗权限
            // Toast.makeText(context, "没有出现在应用上层的权限，无法使用调试功能", Toast.LENGTH_SHORT).show()
            //return this
        }
        ClassLoadObserve
        ChangeApplyManager.init(ctx, getBuildIdentification())
        // DexLoadManager.init(ctx)
        // 替换换肤框架的MergeResource对象
        ResourcesProviderManager.replaceResourceObjectCreator(object : ResourceObjectCreator {
            override fun createResourceObject(
                asset: IAsset,
                defaultContext: Context,
                themeIds: IntArray,
            ): MergeResource {
                return ViewDebugMergeResource(asset, defaultContext.resources, themeIds)
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
                    val info = res.getLayoutInfo(attributeSet)
                    if (info != null) {
                        view.setViewDebugInfo(ViewDebugInfo(info.layoutId, info.invokeTrace))
                    }
                }
            }
        })
        // ViewDebug初始化
        WindowControlManager.init(context.applicationContext as Application)
        // 编译初始化
        launch(Dispatchers.IO) {
            AndroidXmRuleManager.init(ctx)
            // CompileTest.main()
            RemoteFileReceiver.init()
            // PackAssetsFile.clearCachedXml(ctx)
        }
        // ServerManager.init()
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

    /**
     * 获取构建接口，外部重载这个方法，返回正确的构建id
     */
    open fun getBuildIdentification(): IBuildIdentification? = null

    companion object {
        lateinit var ctx: Application
            private set
    }
}
