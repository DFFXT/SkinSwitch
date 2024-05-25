package com.example.viewdebug.server

import org.json.JSONObject

/**
 * 远程控制
 */
object RemoteControl {

    const val TYPE_XMl = "xml"
    const val TYPE_CLASS = "class"

    /**
     * android studio 打开一个文件
     */
    fun openFile(name: String, type: String, callback: ((Boolean) ->Unit)? = null) {
        if (ServerManager.isConnected()) {
            val content = JSONObject()
                .put("type", type)
                .put("name", name)
                .toString()
            ServerManager.send("open", content, onError = {
                callback?.invoke(false)
            }, callback = {
                callback?.invoke(true)
            })
        }
    }

    fun openXml(name: String, callback: ((Boolean) -> Unit)? = null) {
        openFile(name, TYPE_XMl, callback)
    }

    fun openClass(name: String, callback: ((Boolean) -> Unit)? = null) {
        openFile(name, TYPE_CLASS, callback)
    }
}