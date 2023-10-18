package com.example.viewdebug.remote

import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.util.launch
import kotlinx.coroutines.Dispatchers

internal class DefaultDexFileListener : RemoteFileReceiver.FileWatcher(TYPE_DEX, consume = true) {
    override fun onReceive(fileContainer: FileContainer) {
        // 刷新更改列表
        DexLoadManager.hotApply()
        launch(Dispatchers.Main) {
            WindowControlManager.refreshModifyListPage()
            WindowControlManager.notifyModifyList()
        }
    }
}