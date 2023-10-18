package com.example.viewdebug.remote

import com.example.viewdebug.xml.AndroidXmRuleManager
import com.skin.log.Logger

/**
 * 监听特殊文件
 */
internal class SpecialFileListener : RemoteFileReceiver.FileWatcher(TYPE_RULES, consume = true) {
    override fun onReceive(fileContainer: FileContainer) {
        // 如果是rules文件，则拦截应用
        fileContainer.fileInfo.forEach { fileInfo ->
            Logger.i("SpecialFileListener", "receive：" + fileInfo.path)
            AndroidXmRuleManager.addRuleFile(fileInfo.path)
        }
    }
}