package com.example.viewdebug.remote

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.ChangeApplyManager
import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import kotlinx.coroutines.Dispatchers
import java.io.File

internal class DefaultDexFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        if (fileInfo.type == RemoteFileReceiver.FileWatcher.TYPE_DEX) {
            // val originPath = fileInfo.originPath ?: return false
            // 获取远程文件路径
            /*val remoteFileName = File(originPath).name
            launch(Dispatchers.Main) {
                ViewDebugInitializer.ctx.getString(R.string.view_debug_file_receive_tip, File(remoteFileName).name).shortToast()
            }*/
            // 刷新更改列表
            DexLoadManager.dexMove()
            WindowControlManager.refreshModifyListPage()
            WindowControlManager.notifyModifyList()
            return true
        }
        // 返回false，走默认提示
        return false
    }
}