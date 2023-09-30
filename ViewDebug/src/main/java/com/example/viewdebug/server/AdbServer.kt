package com.example.viewdebug.server

import com.example.viewdebug.R
import com.example.viewdebug.server.route.BizRequest404Route
import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.readBytes
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import java.io.DataInputStream
import java.net.ServerSocket

/**
 * socket协议
 * 请求：
 * 4字节，路由id长度
 * 路由id内容
 * 4字节，body长度
 * body内容
 *
 * 返回：
 * 4字节 body长度
 * body内容
 */
internal class AdbServer {
    companion object {
        private const val TAG = "AdbServer"
    }

    private lateinit var server: ServerSocket
    private val bizMap = HashMap<String, Class<BizRoute>>()

    fun init(port: Int) {
        launch(Dispatchers.IO) {
            server = ServerSocket(port)
            Logger.d(TAG, "listen: $port")
            while (true) {
                val socket = server.accept()
                Logger.d(TAG, "accept ${socket.remoteSocketAddress}")
                launch(Dispatchers.IO) {
                    val input = DataInputStream(socket.getInputStream())
                    // 接收内容长度
                    val contentLength = input.readInt()
                    // 路由id
                    val routeId = String(input.readBytes(contentLength))
                    // 构建路由处理器
                    val routeClass = bizMap.getOrDefault(routeId, BizRequest404Route::class.java)
                    // 处理器处理对应请求
                    routeClass.newInstance().onRequest(routeId, RequestImpl(input), ResponseWriterImpl(socket))
                }
            }
        }

    }

    fun <T: BizRoute> addBizRoute(routeId: String, routeClass: Class<T>) {
        bizMap[routeId] = routeClass as Class<BizRoute>
    }

    fun destroy() {
        if (this::server.isInitialized) {
            server.close()
        }
    }



}