package com.example.viewdebug.remote

import android.content.Context
import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.skin.ViewDebugMergeResource
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.skin.log.Logger
import com.skin.skincore.asset.DefaultResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

/**
 * 拦截xml编译xml，目前支持drawable、layout
 */
class DefaultXmlFileListener : RemoteFileReceiver.FileWatcher {
    override fun onChange(path: String, type: String?): Boolean {
        if (path.endsWith(".xml")) {
            val ctx = ViewDebugInitializer.ctx
            val file = File(path)
            when (type) {
                "layout",
                "drawable" -> {
                    launch(Dispatchers.IO) {
                        val id = ctx.resources.getIdentifier(file.nameWithoutExtension, type, ctx.packageName)
                        Logger.i("DefaultXmlFileListener", "$path $type $id")
                        if (id != 0) {
                            if (compileXml(ctx, file.inputStream(), id, type)) {
                                withContext(Dispatchers.Main) {
                                    ctx.getString(R.string.view_debug_file_receive_xml_ok_tip, file.name).shortToast()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    ctx.getString(R.string.view_debug_file_receive_xml_err_tip, file.name).shortToast()
                                }
                            }
                        }
                    }
                    return true
                }
            }

        }
        return false
    }


    private suspend fun compileXml(ctx: Context, inputStream: InputStream, resourceId: Int, resourceType: String): Boolean {
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
                ViewDebugMergeResource.interceptedAsset = assetManager.second
                ViewDebugMergeResource.addInterceptor(resourceType, resourceId)
                ViewDebugMergeResource.layoutInterceptorMapper.add(resourceId)
            }
            return true
        } catch (e: Exception) {
            Logger.e("XmlCompiler", "compile error")
            e.printStackTrace()
            return false
        }
    }
}