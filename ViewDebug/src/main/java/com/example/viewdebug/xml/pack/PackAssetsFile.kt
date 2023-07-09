package com.example.viewdebug.xml.pack

import com.example.viewdebug.ViewDebugInitializer
import com.skin.log.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 打包apk，在已有的apk上插入asset文件
 */
class PackAssetsFile {

    companion object {
        const val TYPE_LAYOUT = "layout"
    }

    private fun getApkUnZipFolder() =
        ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + File.separator + "apk"

    /**
     * 生成的apk地址
     */
    fun getPackedApkPath() =
        ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + File.separator + "view_debug.apk"


    /**
     * 将当前数据流写入apk的assets目录
     * @param name 文件名称，不带后缀
     */
    fun addLayoutFile(inputStream: InputStream, name: String) {
        val dir =
            File(getApkUnZipFolder() + File.separator + "assets" + File.separator + TYPE_LAYOUT)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "$name.xml")
        IOUtil.copy(inputStream, FileOutputStream(file), ByteArray(1024 * 1024))
    }

    /**
     * 打包生成apk（生成压缩包）
     */
    fun pack() {
        Logger.i("PackAssetsFile", "apk: ${getPackedApkPath()}   apkFolder: ${getApkUnZipFolder()}")
        IOUtil.zip(getPackedApkPath(), File(getApkUnZipFolder()))
    }

}