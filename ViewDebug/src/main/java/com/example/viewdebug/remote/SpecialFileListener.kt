package com.example.viewdebug.remote

import com.example.viewdebug.xml.AndroidXmRuleManager
import com.skin.log.Logger

/**
 * 监听特殊文件
 */
class SpecialFileListener: RemoteFileReceiver.FileWatcher {
    override fun onChange(path: String, type: String?): Boolean {
        // 如果是rules文件，则拦截应用
        if (type == "rules") {
            Logger.i("SpecialFileListener", "receive："+path)
            AndroidXmRuleManager.addRuleFile(path)
            return true
        }
        return false
    }
}