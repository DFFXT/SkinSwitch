package com.example.viewdebug.remote

import com.example.viewdebug.xml.AndroidXmRuleManager
import com.skin.log.Logger

/**
 * 监听特殊文件
 */
internal class SpecialFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        // 如果是rules文件，则拦截应用
        if (fileInfo.type == RemoteFileReceiver.FileWatcher.TYPE_RULES) {
            Logger.i("SpecialFileListener", "receive："+fileInfo.path)
            AndroidXmRuleManager.addRuleFile(fileInfo.path)
            return true
        }
        return false
    }
}