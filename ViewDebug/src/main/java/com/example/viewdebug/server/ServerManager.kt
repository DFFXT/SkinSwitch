package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRequestPackageRoute
import com.example.viewdebug.server.route.BizRequestPushConfigRoute
import com.example.viewdebug.server.route.BizRequestRClassRoute

object ServerManager {
    private var adbServer: AdbServer? = AdbServer()

    fun init() {
        adbServer?.init(12349)
        adbServer?.addBizRoute("request/pkgName", BizRequestPackageRoute::class.java)
        adbServer?.addBizRoute("request/R", BizRequestRClassRoute::class.java)
        adbServer?.addBizRoute("request/requestPushAgreement", BizRequestPushConfigRoute::class.java)
    }

    fun destroy() {
        adbServer?.destroy()
    }
}