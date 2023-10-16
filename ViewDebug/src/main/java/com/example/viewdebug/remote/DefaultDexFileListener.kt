package com.example.viewdebug.remote

import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.ui.WindowControlManager

internal class DefaultDexFileListener: RemoteFileReceiver.FileWatcher {
    override fun onReceive(fileContainer: RemoteFileReceiver.FileWatcher.FileContainer): Boolean {

        if (fileContainer.fileInfo.find { it.type == RemoteFileReceiver.FileWatcher.TYPE_DEX } != null) {

            // 刷新更改列表
            DexLoadManager.hotApply()
            WindowControlManager.refreshModifyListPage()
            WindowControlManager.notifyModifyList()
            return true
        }
        // 返回false，走默认提示
        return false
    }
}