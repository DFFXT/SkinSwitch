package com.example.viewdebug.server.route

import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.server.ResponseWriter

/**
 * 请求adb推送配置
 * 数据格式：
 * version=1.0
 * pkgName=com.xx.xxx
 * destDir=xxxxx
 * listenFile=xxxx
 */
internal class BizRequestPushConfigRoute : BizRoute {
    override fun onRequest(routeId: String, content: String, response: ResponseWriter) {

        val agreement = RemoteFileReceiver.getAgreement().toByteArray()
        response.writeContentLength(agreement.size)
        response.write(agreement)
        response.finish()
    }
}