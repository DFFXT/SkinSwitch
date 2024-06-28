package com.example.viewdebug.server.route

import com.example.viewdebug.server.ResponseWriter
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import kotlinx.coroutines.Dispatchers

/**
 * 远程提示
 */
internal class BizRequestTipsRoute:BizRoute {
    override fun onRequest(routeId: String, content: String, response: ResponseWriter) {
        response.finish()
        launch(Dispatchers.Main) {
            if (content.isNotEmpty()) {
                content.shortToast()
            }
        }
    }
}