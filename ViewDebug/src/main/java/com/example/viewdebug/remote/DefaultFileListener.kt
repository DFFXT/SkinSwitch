package com.example.viewdebug.remote

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import kotlinx.coroutines.Dispatchers
import java.io.File

/**
 * 默认文件提示
 */
internal class DefaultFileListener: RemoteFileReceiver.FileWatcher(consume = false) {
    override fun onReceive(fileContainer: RemoteFileReceiver.FileWatcher.FileContainer) {
        launch(Dispatchers.Main) {
            val files = fileContainer.fileInfo.joinToString(separator = "\n") { File(it.path).name }
            ViewDebugInitializer.ctx.getString(R.string.view_debug_file_receive_tip, files).shortToast()
        }
    }
}