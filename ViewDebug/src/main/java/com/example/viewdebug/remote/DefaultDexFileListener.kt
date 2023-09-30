package com.example.viewdebug.remote

import com.example.viewdebug.apply.dex.DexLoadManager
import com.example.viewdebug.ui.WindowControlManager

internal class DefaultDexFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        if (fileInfo.type == RemoteFileReceiver.FileWatcher.TYPE_DEX) {
            // val originPath = fileInfo.originPath ?: return false
            // 获取远程文件路径
            /*val remoteFileName = File(originPath).name
            launch(Dispatchers.Main) {
                ViewDebugInitializer.ctx.getString(R.string.view_debug_file_receive_tip, File(remoteFileName).name).shortToast()
            }*/
            /*val extra = fileInfo.extra
            if (extra != null) {
                val classList = extra.getJSONArray("class")
                val classState = HashMap<String, Boolean>()
                for (i in 0 until classList.length()) {
                    var name = classList.getString(i).replace('/', '.')
                    name = name.substring(0, name.length - 6)
                    classState[name] = ClassLoadObserve.isLoaded(name)
                }
                Logger.i("s", "")
            }*/
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