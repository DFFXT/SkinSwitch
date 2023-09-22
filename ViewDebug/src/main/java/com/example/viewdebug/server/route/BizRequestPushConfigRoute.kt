package com.example.viewdebug.server.route

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.remote.RemoteFileReceiver
import com.example.viewdebug.server.Request
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
    override fun onRequest(routeId: String, request: Request, response: ResponseWriter) {
        // 接收文件路径
        val builder = StringBuilder()
        builder.append("version=1.0\n")
        builder.append("pkgName=${ViewDebugInitializer.ctx.packageName}\n")
        builder.append("destDir=${RemoteFileReceiver.getReceivePath(ViewDebugInitializer.ctx)}\n")
        builder.append("listenFile=${RemoteFileReceiver.watchingConfigFile}")
        val content = builder.toString().toByteArray()
        response.writeContentLength(content.size)
        response.write(content)
        response.finish()
    }
}