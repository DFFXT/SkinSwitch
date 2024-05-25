package com.example.viewdebug.server

/**
 * 远程控制
 */
object RemoteControl {

    /**
     * android studio 打开一个文件
     */
    fun openFile(fileName: String, callback: ((Boolean) ->Unit)? = null) {
        if (ServerManager.isConnected()) {
            ServerManager.send("open", fileName, onError = {
                callback?.invoke(false)
            }, callback = {
                callback?.invoke(true)
            })
        }
    }
}