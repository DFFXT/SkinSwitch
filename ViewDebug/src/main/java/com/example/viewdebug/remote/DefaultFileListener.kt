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
class DefaultFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(path: String, type: String?): Boolean {
        launch(Dispatchers.Main) {
            ViewDebugInitializer.ctx.getString(R.string.view_debug_file_receive_tip, File(path).name).shortToast()
        }
        return true
    }
}