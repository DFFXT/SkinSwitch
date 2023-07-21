package com.example.viewdebug.remote

import android.os.FileObserver
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.util.launch
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

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
    private val watchBase = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + File.separator + "view-debug"
    private val watchingReceivePath = watchBase + File.separator + "receive"

    // 配置文件
    private val watchingConfigPath = watchBase + File.separator + "view-debug-config.json"

    private val fileWatcher = LinkedHashSet<FileWatcher>()

    init {
        val file = File(watchingReceivePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        startWatch()
    }

    private fun startWatch() {
        fileObserver = object : FileObserver(watchingConfigPath, ATTRIB) {
            override fun onEvent(event: Int, path: String?) {
                if (event == ATTRIB) {
                    launch(Dispatchers.IO) {
                        // 当监听到文件变化时，需要重新监听
                        fileObserver?.stopWatching()
                        delay(1000)
                        val content = String(File(watchingConfigPath).readBytes())
                        val json = JSONObject(content)
                        val receivePath = json.getString("file") ?: return@launch
                        Logger.i("RemoteFileReceiver", receivePath)
                        startWatch()
                        val watchers = fileWatcher.reversed()
                        // 只有一个能处理，拦截了后续监听则不处理
                        withContext(Dispatchers.Main) {
                            for (watcher in watchers) {
                                if (watcher.onChange(receivePath)) {
                                    break
                                }
                            }
                        }
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

    internal interface FileWatcher {
        fun onChange(path: String): Boolean
    }
}