package com.example.viewdebug.server.route

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.server.Request
import com.example.viewdebug.server.ResponseWriter

/**
 * 请求包名
 */
internal class BizRequestPackageRoute:BizRoute {
    override fun onRequest(routeId: String, request: Request, response: ResponseWriter) {
        val packageNameByteArray = ViewDebugInitializer.ctx.packageName.toByteArray()
        response.writeContentLength(packageNameByteArray.size)
        response.write(packageNameByteArray)
        response.finish()
    }
}