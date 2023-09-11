package com.example.viewdebug.xml

import android.content.Context
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.skin.log.Logger
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.DefaultResourceLoader
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.collector.getViewUnion
import java.io.InputStream

internal object XmlManager {
    /**
     * 编译xml，并打包，然后再加载
     */
    internal suspend fun compileXmlAndApply(ctx: Context, inputStream: InputStream, resourceId: Int, resourceType: String): Boolean {
        try {
            val compiler = XmlCompiler(ctx)
            val buffer = compiler.compile(inputStream)
            val byteArray = ByteArray(buffer.limit())
            buffer.get(byteArray, 0, buffer.limit())
            // 打包
            val pack = PackAssetsFile(ctx)
            pack.addAXMLFile(byteArray.inputStream(), resourceId.toString())
            pack.pack()
            // 读入
            val assetManager = DefaultResourceLoader().createAssetManager(pack.getPackedApkPath(), ctx)
            if (assetManager != null) {
                ViewDebugResourceManager.interceptedAsset = assetManager.second
                ViewDebugResourceManager.addInterceptor(resourceType, resourceId)
            }
            return true
        } catch (e: Exception) {
            Logger.e("XmlCompiler", "compile error")
            e.printStackTrace()
            return false
        }
    }

    /**
     * 如果view使用了这个id，则刷新
     */
    internal fun applyGlobalViewByResId(resId: Int) {
        val event = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)
        SkinManager.getAllViews().forEach {
            val union = it.getViewUnion()
            if (union != null) {
                for (attr in union) {
                    if (attr.resId == resId) {
                        // 构建viewUnion
                        val tempUnion = ViewUnion()
                        tempUnion.addAttr(attr)
                        AttrApplyManager.apply(event, it, tempUnion, SkinManager.getResourceProvider(it.context))
                    }
                }
            }
        }
    }
}