package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRequestPackageRoute
import com.example.viewdebug.server.route.BizRequestPushConfigRoute
import com.example.viewdebug.server.route.BizRequestRClassRoute

object ServerManager {
    private var adbServer: AdbServer? = AdbServer()
    private var adbClient: AdbClient? = AdbClient()


    fun init() {
        adbServer?.init()
        adbClient?.init()

        adbServer?.addBizRoute("request/pkgName", BizRequestPackageRoute::class.java)
        adbServer?.addBizRoute("request/R", BizRequestRClassRoute::class.java)
        adbServer?.addBizRoute("request/requestPushAgreement", BizRequestPushConfigRoute::class.java)
    }


    fun send(cmd: String, content: String, callback: (String) -> Unit) {
        adbClient?.send(cmd, content, callback)
    }

    fun getServerPort() = adbServer?.getPort()
    fun getClientPort() = adbClient?.getPort()

    fun destroy() {
        adbServer?.destroy()
    }
}