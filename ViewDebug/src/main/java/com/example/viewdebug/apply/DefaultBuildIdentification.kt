package com.example.viewdebug.apply

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.remote.RemoteFileReceiver
import java.io.File

/**
 * 默认的构建规则
 * 如果存在信号文件[RemoteFileReceiver.CLEAR_SIGNAL],则代表这次运行需要清空更改
 * 否则保留更改
 */
class DefaultBuildIdentification : IBuildIdentification {
    override fun getBuildId(): IBuildIdentification.BuildType {
        val clearSignal = File(
            RemoteFileReceiver.getReceivePath(ViewDebugInitializer.ctx),
            RemoteFileReceiver.CLEAR_SIGNAL
        )
        if (clearSignal.exists()) {
            clearSignal.delete()
            return IBuildIdentification.BuildType.BUILD_ID_CLEAR
        }
        return IBuildIdentification.BuildType.BUILD_ID_HOLD
    }
}