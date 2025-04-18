package com.example.viewdebug.remote

import android.content.Context
import android.os.FileObserver
import com.example.viewdebug.BuildConfig
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.server.ServerManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.makeAsDir
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    // 清除文件名称
    const val CLEAR_SIGNAL = "clear_signal"

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
        specialWatchers.add(DefaultLaunchFileListener())
        defaultFileWatchers.add(DefaultXmlFileListener())
        defaultFileWatchers.add(DefaultDexFileListener())
        defaultFileWatchers.add(ValueXMlListener())
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
                        delay(300)
                        val content = String(File(watchingConfigFile).readBytes())
                        val json = JSONObject(content)
                        val arr = json.getJSONArray("config")
                        val fileContainer = FileWatcher.FileContainer()
                        if (json.has("reboot")) {
                            fileContainer.reboot = json.getBoolean("reboot")
                        }
                        val watchers = fileWatcher.reversed()
                        for (i in 0 until arr.length()) {
                            val item = arr.getJSONObject(i)
                            val receivePath = item.getString("file") ?: return@launch
                            val fileType = item.getString("type")
                            val originPath = item.getString("originPath")
                            fileContainer.fileInfo.add(
                                FileWatcher.FileInfo(receivePath, fileType, originPath)
                            )
                        }

                        Logger.i("RemoteFileReceiver", json.toString())
                        // 只有一个能处理，拦截了后续监听则不处理
                        var container = fileContainer
                        val watcherCollection = LinkedList<FileWatcher>()
                        watcherCollection.addAll(specialWatchers)
                        watcherCollection.addAll(watchers)
                        watcherCollection.addAll(defaultFileWatchers)
                        for (watcher in watcherCollection) {
                            container = watcher.process(container)
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

    /**
     * 插件存储区域，插件的所有存储都应在这个文件夹下
     */
    fun getBasePath(ctx: Context): String {
        return ctx.externalCacheDir!!.absolutePath + File.separator + "view-debug"
    }

    /**
     * 文件接受的文件夹
     */
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
        val agreement = getAgreement()
        Logger.d("write agreement", agreement)
        agreementFile.writeText(agreement)
    }

    fun getAgreement(): String {
        val builder = StringBuilder()
        builder.append("version=${BuildConfig.gitVersion}\n")
        // 包名
        builder.append("pkgName=${ViewDebugInitializer.ctx.packageName}\n")
        // 推送文件存放的文件夹
        builder.append("destDir=${watchingReceivePath}\n")
        // 推送监听文件地址
        builder.append("listenFile=${watchingConfigFile}\n")
        // 清空所有更改信号文件名称
        builder.append("clearSignalFileName=${CLEAR_SIGNAL}\n")
        // 本地socket服务器端口
        builder.append("serverPort=${ServerManager.getServerPort() ?: 0}\n")
        // 本地socket客户端端口
        builder.append("clientPort=${ServerManager.getClientPort() ?: 0}\n")
        return builder.toString()
    }

    /**
     * @param types 能够处理哪些类型的文件, 如果为空则，处理所有类型
     * @param consume 是否需要消耗这些文件，true则消耗，不传递给下个监听
     */
    internal abstract class FileWatcher(protected vararg val types: String, protected val consume: Boolean) {
        // receive
        abstract fun onReceive(fileContainer: FileContainer)

        fun process(fileContainer: FileContainer): FileContainer {
            val infos = if (types.isEmpty()) {
                fileContainer.fileInfo
            } else {
                fileContainer.fileInfo.filter { it.type in types }
            }
            if (infos.isNotEmpty()) {
                val container = FileContainer()
                container.fileInfo.addAll(infos)
                container.reboot = fileContainer.reboot
                onReceive(container)
            }
            if (consume) {
                fileContainer.fileInfo.removeAll { it.type in types }
            }
            return fileContainer
        }

        /**
         * 接受到的配置文件信息
         */
        class FileContainer {
            val fileInfo: LinkedList<FileInfo> = LinkedList()
            var reboot: Boolean = false
        }

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
            // 未实装
            const val TYPE_ANIM = "anim"
            const val TYPE_FILE = "file"
            const val TYPE_DEX = "dex"
            // 规则文件
            const val TYPE_RULES = "rules"

            // 远程配置文件（Android studio需要传递给应用的一些信息）
            const val TYPE_REMOTE_CONFIG = "config"
            // launch信号
            const val TYPE_LAUNCH = "launch"
            @Deprecated("不计划实装, 无该功能")
            const val TYPE_VALUES_XML = "values"

        }
    }
}