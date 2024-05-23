package com.example.viewdebug.server.route

import com.example.viewdebug.server.Request
import com.example.viewdebug.server.ResponseWriter

internal interface BizRoute {
        fun onRequest(routeId: String, content: String, response: ResponseWriter)
    }