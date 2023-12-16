package com.example.viewdebug.remote

/**
 * 拦截launch信号
 */
internal class DefaultLaunchFileListener : RemoteFileReceiver.FileWatcher(
    TYPE_LAUNCH,
    consume = true
) {

    override fun onReceive(fileContainer: FileContainer) {
        // 不做任何操作，重启信号会在应用启动时进行检查
    }


}