package com.example.viewdebug.server.route

import com.example.viewdebug.server.Request
import com.example.viewdebug.server.ResponseWriter
import java.io.InputStream

internal class BizRequest404Route: BizRoute {

    override fun onRequest(routeId: String, request: Request, response: ResponseWriter) {
        val content = "404 $routeId not found".toByteArray()
        response.writeContentLength(content.size)
        response.write(content)
        response.finish()
    }
}