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
internal class DefaultFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        launch(Dispatchers.Main) {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_file_receive_tip, File(fileInfo.path).name).shortToast()
        }
        return true
    }
}