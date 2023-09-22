package com.example.viewdebug.remote

import android.content.Context
import android.os.FileObserver
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.makeAsDir
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.LinkedList

/**
 * 监听文件
 * config.json格式：
 * {
 *  type:"xx",
 *  file:"xxx"
 * }
 */
internal object RemoteFileReceiver {
    private var fileObserver: FileObserver? = null
    private val watchBase = getBasePath(ViewDebugInitializer.ctx)
    private val watchingReceivePath = getReceivePath(ViewDebugInitializer.ctx)

    private val agreementFile: File by lazy {
        val file = (ViewDebugInitializer.ctx.cacheDir.absolutePath + File.separator + "viewDebug").makeAsDir()
        File(file, "agreement")
    }

    // 配置文件
    val watchingConfigFile = watchBase + File.separator + "view-debug-config.json"

    // 特殊文件监听器
    private val specialWatchers = LinkedList<FileWatcher>()

    // 文件监听
    private val fileWatcher = LinkedHashSet<FileWatcher>()

    // 默认的文件接收处理器
    private val defaultFileWatchers = LinkedList<FileWatcher>()

    fun init() {
        specialWatchers.add(SpecialFileListener())
        defaultFileWatchers.add(DefaultXmlFileListener())
        defaultFileWatchers.add(DefaultFileListener())
        // io线程操作
        launch(Dispatchers.IO) {
            watchingReceivePath.makeAsDir()
            // 判断是否存在，FileObserver只能监听已存在的文件
            val watchTarget = File(watchingConfigFile)
            if (!watchTarget.exists()) {
                watchTarget.createNewFile()
            }
            startWatch()
            writeAgreement()
        }
    }

    private fun startWatch() {
        fileObserver = object : FileObserver(watchingConfigFile, ATTRIB) {
            override fun onEvent(event: Int, path: String?) {
                if (event == ATTRIB) {
                    launch(Dispatchers.IO) {
                        // 当监听到文件变化时，需要重新监听
                        fileObserver?.stopWatching()
                        delay(1000)
                        val content = String(File(watchingConfigFile).readBytes())
                        val json = JSONObject(content)
                        val arr = json.getJSONArray("config")

                        val watchers = fileWatcher.reversed()
                        for (i in 0 until arr.length()) {
                            val item = arr.getJSONObject(i)
                            val receivePath = item.getString("file") ?: return@launch
                            val fileType = item.getString("type")
                            val originPath = item.getString("originPath")
                            val fileInfo = FileWatcher.FileInfo(receivePath, fileType, originPath)
                            Logger.i("RemoteFileReceiver", receivePath)
                            // 只有一个能处理，拦截了后续监听则不处理
                            withContext(Dispatchers.Main) {
                                var resolved = false
                                for (watcher in specialWatchers) {
                                    if (watcher.onChange(fileInfo)) {
                                        resolved = true
                                        break
                                    }
                                }
                                if (!resolved) {
                                    for (watcher in watchers) {
                                        if (watcher.onChange(fileInfo)) {
                                            resolved = true
                                            break
                                        }
                                    }
                                }

                                if (!resolved) {
                                    // 未解决，尝试使用默认处理器
                                    for (watcher in defaultFileWatchers) {
                                        if (watcher.onChange(fileInfo)) {
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        startWatch()
                    }

                }
            }
        }
        fileObserver?.startWatching()
    }

    fun observe(watcher: FileWatcher) {
        fileWatcher.add(watcher)
    }

    fun remove(watcher: FileWatcher) {
        fileWatcher.remove(watcher)
    }

    fun getBasePath(ctx: Context): String {
        return ctx.externalCacheDir!!.absolutePath + File.separator + "view-debug"
    }

    fun getReceivePath(ctx: Context): String {
        return getBasePath(ctx) + File.separator + "receive"
    }

    fun clearReceiveCache() {
        File(watchingReceivePath).deleteRecursively()
    }

    /**
     * 写入协议，给idea插件读取，路径固定
     */
    private fun writeAgreement() {
        if (!agreementFile.exists()) {
            agreementFile.createNewFile()
        }
        val builder = StringBuilder()
        builder.append("version=1\n")
        // 包名
        builder.append("pkgName=${ViewDebugInitializer.ctx.packageName}\n")
        // 推送文件存放的文件夹
        builder.append("destDir=${watchingReceivePath}\n")
        // 推送监听文件地址
        builder.append("listenFile=${watchingConfigFile}")
        agreementFile.writeText(builder.toString())
    }

    internal interface FileWatcher {
        fun onChange(fileInfo: FileInfo): Boolean

        /**
         * @param path 接收文件路径
         * @param type 文件类型[TYPE_FILE]
         * @param originPath 文件对应远程文件路径
         */
        class FileInfo(val path: String, val type: String?, val originPath: String?)

        companion object {
            const val TYPE_LAYOUT = "layout"
            const val TYPE_DRAWABLE = "drawable"
            const val TYPE_COLOR = "color"
            const val TYPE_ANIM = "anim"
            const val TYPE_FILE = "file"
            const val TYPE_DEX = "dex"
            const val TYPE_RULES = "rules"
        }
    }
}