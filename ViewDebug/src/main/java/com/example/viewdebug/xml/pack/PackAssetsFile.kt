package com.example.viewdebug.xml.pack

import android.content.Context
import com.example.viewdebug.ViewDebugInitializer
import com.skin.log.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * 打包apk，在已有的apk上插入asset文件
 */
class PackAssetsFile(private val ctx: Context) {

    companion object {
        const val TYPE_LAYOUT = "layout"
    }

    private fun getApkUnZipFolder() =
        ctx.externalCacheDir!!.absolutePath + File.separator + "apk"

    /**
     * 生成的apk地址
     */
    fun getPackedApkPath() =
        ctx.externalCacheDir!!.absolutePath + File.separator + "view_debug.apk"


    /**
     * 将当前数据流写入apk的assets目录
     * @param name 文件名称，不带后缀
     */
    suspend fun addLayoutFile(inputStream: InputStream, name: String) {
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
    suspend fun pack() {
        Logger.i("PackAssetsFile", "apk: ${getPackedApkPath()}   apkFolder: ${getApkUnZipFolder()}")
        checkApkResources()
        IOUtil.zip(getPackedApkPath(), File(getApkUnZipFolder()))
    }

    /**
     * 检测是否存在apk资源，没有的话会进行资源拷贝
     */
    private fun checkApkResources() {
        val apkFolder = getApkUnZipFolder()
        val dir = File(apkFolder, "androidManifest.xml")
        if (!dir.exists()) {
            val buffer = ByteArray(1024 * 1024)
            // 将assets/apk目录下的所有文件拷贝到外部目录
            val dir = File(apkFolder)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ctx.resources.assets.list("apk")?.forEach {
                fileCopy("apk", it, apkFolder, buffer)
            }
        }
    }

    private fun fileCopy(basePath: String, path: String, toDir: String, buffer: ByteArray) {
        val children =  ctx.assets.list(basePath + File.separator +path)

        if (children.isNullOrEmpty()) {
            // 有可能是文件
            try {
                val toFile = File(toDir, path)
                // 是文件
                IOUtil.copy(ctx.assets.open(basePath + File.separator + path), toFile.outputStream(), buffer)
            } catch (e: Exception) {
                // 是一个空文件夹
            }
        } else {
            val dest = File(toDir, path)
            if (!dest.exists()) {
                dest.mkdirs()
            }
            children.forEach {
                fileCopy(basePath, path + File.separator + it, toDir, buffer)
            }
        }
    }

}