package com.example.viewdebug.apply.xml

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.makeAsDir
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream


internal object XmlLoadManager {
    // 理论上是可以不用额外copy缓存的 todo
    private val tmpApk by lazy {
        val apk = File(PackAssetsFile.getPackedApkPath(ctx))
        val file = File(apk.parent!!, "view-debug-tmp.apk")
        file
    }

    private val valuesFile by lazy {
        val dir =
            (RemoteFileReceiver.getBasePath(ViewDebugInitializer.ctx) + File.separator + "values-xml").makeAsDir()
        File(dir, "values")
    }
    private lateinit var ctx: Application

    private val pack by lazy {
        PackAssetsFile(ctx)
    }

    fun init(ctx: Application, loadApk: Boolean, useOnce: Boolean) {
        this.ctx = ctx
        initValues(loadApk, useOnce)
        initApk(loadApk, useOnce)
    }

    /**
     * 根据参数判断是否加载apk
     */
    private fun initApk(load: Boolean, useOnce: Boolean) {
        val apk = File(PackAssetsFile.getPackedApkPath(XmlLoadManager.ctx))
        if (!apk.exists()) {
            // 没有apk，直接返回
            return
        }
        if (!load) {
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
                deleteValues()
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
        val assetManager = DefaultResourceLoader().createAssetManager(apkPath, ctx)
        if (assetManager != null) {
            readXmlList(assetManager.second)
        }
    }

    private fun readXmlList(assetManager: AssetManager) {
        val rids = PackAssetsFile.getResourceId(ctx)
        if (rids.isEmpty()) return
        ViewDebugResourceManager.interceptedAsset = assetManager
        rids.forEach {
            ViewDebugResourceManager.addInterceptor(ctx.resources.getResourceTypeName(it), it)
        }
    }

    private fun initValues(load: Boolean, useOnce: Boolean) {
        if (load) {
            readValue()
            if (useOnce) {
                deleteValues()
            }
        } else {
            deleteValues()
        }
    }

    /**
     * 读取本地尺存储的values文件
     */
    private fun readValue() {
        if (valuesFile.exists()) {
            val array = JSONArray(valuesFile.readText())
            var item: JSONObject
            for (i in 0 until array.length()) {
                item = array.getJSONObject(i)
                ViewDebugResourceManager.addValuesInterceptor(
                    item.getInt("id"),
                    item.getString("value")
                )
            }
        }
    }

    /**
     * 保存xml-values数据,
     */
    internal fun saveValues() {
        val jsonArray = JSONArray()
        ViewDebugResourceManager.getAllValueChangedItem().forEach {
            jsonArray.put(
                JSONObject()
                    .put("id", it.key)
                    .put("value", it.value)
            )
        }
        valuesFile.writeText(jsonArray.toString())
    }

    /**
     * 删除value资源
     */
    fun deleteValues() {
        if (valuesFile.exists()) {
            valuesFile.delete()
        }
    }

    internal fun compileXml(
        ctx: Context,
        inputStream: InputStream,
        resourceId: Int,
        resourceType: String
    ): Boolean {
        return try {
            val compiler = XmlCompiler(ctx)
            val buffer = compiler.compile(inputStream)
            val byteArray = ByteArray(buffer.limit())
            buffer.get(byteArray, 0, buffer.limit())
            pack.addAXMLFile(byteArray.inputStream(), resourceId.toString())
            true
        } catch (e: Exception) {
            Logger.e("XmlCompiler", "compile error")
            e.printStackTrace()
            false
        }
    }

    internal fun compileApk() {
        pack.pack()
    }

    /**
     * 编译xml，并打包，然后再加载
     */
    internal fun loadApk(): Boolean {
        try {
            // 读入
            val assetManager =
                DefaultResourceLoader().createAssetManager(pack.getPackedApkPath(), ctx)
            if (assetManager != null) {
                ViewDebugResourceManager.interceptedAsset = assetManager.second
                PackAssetsFile.getResourceId(ctx).forEach { resourceId ->
                    val resourceType = ctx.resources.getResourceTypeName(resourceId)
                    ViewDebugResourceManager.addInterceptor(resourceType, resourceId)
                }
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
                        AttrApplyManager.apply(
                            event,
                            it,
                            tempUnion,
                            SkinManager.getResourceProvider(it.context)
                        )
                    }
                }
            }
        }
    }
}