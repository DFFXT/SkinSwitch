package com.example.viewdebug.xml.pack

import android.content.Context
import com.skin.log.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 打包apk，在已有的apk上插入asset文件
 */
class PackAssetsFile(private val ctx: Context) {

    companion object {
        const val FOLDER = "compiledXml"

        /**
         * 清除编译缓存
         */
        fun clearCachedXmlAndApk(ctx: Context) {
            val dir = File(getApkUnZipFolder(ctx), "assets")
            if (dir.exists()) {
                dir.listFiles()?.forEach {
                    it.deleteRecursively()
                }
            }
            File(getPackedApkPath(ctx)).delete()
        }

        /**
         * 获取当前apk编译后的xml
         */
        fun getResourceId(ctx: Context): List<Int> {
            return compiledXmlDir(ctx).listFiles()?.mapNotNull { it.nameWithoutExtension.toIntOrNull() } ?: emptyList()
        }

        /**
         * 删除资源文件
         */
        fun deleteResource(ctx: Context, id: Int) {
            File(compiledXmlDir(ctx), "$id.xml").delete()
        }

        /**
         * 编译后的xml地址
         */
        private fun compiledXmlDir(ctx: Context): File {
            return  File(getApkUnZipFolder(ctx) + File.separator + "assets" + File.separator + FOLDER)
        }

        private fun getApkUnZipFolder(ctx: Context) =
            ctx.externalCacheDir!!.absolutePath + File.separator + "apk"

        /**
         * 生成的apk地址
         */
        fun getPackedApkPath(ctx: Context) =
            ctx.externalCacheDir!!.absolutePath + File.separator + "view_debug.apk"
    }

    /**
     * 生成的apk地址
     */
    fun getPackedApkPath() = Companion.getPackedApkPath(ctx)


    /**
     * 将当前数据流写入apk的assets目录
     * @param name 文件名称，不带后缀
     */
    fun addAXMLFile(inputStream: InputStream, name: String) {
        val dir = compiledXmlDir(ctx)
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
        Logger.i("PackAssetsFile", "apk: ${getPackedApkPath()}   apkFolder: ${getApkUnZipFolder(ctx)}")
        checkApkResources()
        IOUtil.zip(getPackedApkPath(), File(getApkUnZipFolder(ctx)))
    }

    /**
     * 检测是否存在apk资源，没有的话会进行资源拷贝
     */
    private fun checkApkResources() {
        val apkFolder = getApkUnZipFolder(ctx)
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
        val children = ctx.assets.list(basePath + File.separator + path)

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