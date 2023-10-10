package com.example.viewdebug.apply.xml

import android.app.Application
import android.content.Context
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.skin.log.Logger
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.DefaultResourceLoader
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.collector.getViewUnion
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.InputStream


internal object XmlLoadManager {
    // 理论上是可以不用额外copy缓存的 todo
    private val tmpApk by lazy {
        val apk = File(PackAssetsFile.getPackedApkPath(ctx))
        val file = File(apk.parent!!, "view-debug-tmp.apk")
        file
    }
    private lateinit var ctx: Application
    fun init(ctx: Application, loadApk: Boolean, useOnce: Boolean) {
        XmlLoadManager.ctx = ctx
        val apk = File(PackAssetsFile.getPackedApkPath(XmlLoadManager.ctx))
        if (!apk.exists()) {
            // 没有apk，直接返回
            return
        }
        if (!loadApk) {
            // 不加载apk，需要删除
            launch(Dispatchers.IO) {
                apk.delete()
                tmpApk.delete()
                PackAssetsFile.clearCachedXmlAndApk(ctx)
            }
            return
        }

        if (useOnce) {
            apk.copyTo(tmpApk, true)
            // 只使用一次，也需要将原始apk删除
            applyApk(tmpApk.absolutePath)
            launch(Dispatchers.IO) {
                PackAssetsFile.clearCachedXmlAndApk(ctx)
            }
        } else {
            // 需要多次使用，可以直接加载原始apk
            applyApk(apk.absolutePath)
        }
    }

    /**
     * 应用打开时，加载可应用的apk
     */
    private fun applyApk(apkPath: String) {
        val rids = PackAssetsFile.getResourceId(ctx)
        if (rids.isEmpty()) return
        rids.forEach {
            val assetManager = DefaultResourceLoader().createAssetManager(apkPath, ctx)
            if (assetManager != null) {
                ViewDebugResourceManager.interceptedAsset = assetManager.second
                ViewDebugResourceManager.addInterceptor(ctx.resources.getResourceTypeName(it), it)
            }
        }
    }
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