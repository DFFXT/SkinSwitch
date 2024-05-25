package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRequestPackageRoute
import com.example.viewdebug.server.route.BizRequestPushConfigRoute
import com.example.viewdebug.server.route.BizRequestRClassRoute

object ServerManager {
    private var adbServer: AdbServer? = AdbServer()
    private var adbClient: AdbClient? = AdbClient()
    private val connectedListener = ArrayList<OnConnectedListener>()


    fun init() {
        adbServer?.init()
        adbClient?.init()
        adbClient?.onConnectedListener = OnConnectedListener {state ->
            connectedListener.forEach {
                it.onConnectedStateChanged(state)
            }
        }

        adbServer?.addBizRoute("request/pkgName", BizRequestPackageRoute::class.java)
        adbServer?.addBizRoute("request/R", BizRequestRClassRoute::class.java)
        adbServer?.addBizRoute("request/requestPushAgreement", BizRequestPushConfigRoute::class.java)
    }


    fun isConnected() :Boolean {
        return adbClient?.isConnected ?: false
    }

    fun send(cmd: String, content: String, callback: (String) -> Unit, onError:(()->Unit)? = null) {
        adbClient?.send(cmd, content, callback, onError)
    }

    fun getServerPort() = adbServer?.getPort()
    fun getClientPort() = adbClient?.getPort()

    fun addConnectedListener(listener: OnConnectedListener) {
        if (!connectedListener.contains(listener)) {
            connectedListener.add(listener)
        }
    }


    fun removeConnectedListener(listener: OnConnectedListener) {
        connectedListener.remove(listener)
    }

    fun destroy() {
        adbServer?.destroy()
    }

    fun interface OnConnectedListener {
        fun onConnectedStateChanged(isConnected: Boolean)
    }
}