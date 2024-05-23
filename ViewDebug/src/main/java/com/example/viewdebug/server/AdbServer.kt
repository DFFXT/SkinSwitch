package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import java.net.ServerSocket
import java.net.Socket
import java.util.LinkedList

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
typealias Callback = (String) -> Unit

internal class AdbServer {
    companion object {
        private const val TAG = "AdbServer"

        // 默认端口
        private const val DEFAULT_SERVER_PORT = 49871
        private const val DEFAULT_CLIENT_PORT = 49872
    }

    private lateinit var server: ServerSocket
    private val bizMap = HashMap<String, Class<BizRoute>>()


    private var sockets: LinkedList<Socket> = LinkedList()

    fun init() {
        server = getServerSocket(DEFAULT_CLIENT_PORT)
        launch(Dispatchers.IO) {
            Logger.d(TAG, "listen port: ${server.localPort}")
            while (true) {
                val socket = server.accept()
                sockets.add(socket)
                Logger.d(TAG, "accept ${socket.remoteSocketAddress}")
                launch(Dispatchers.IO) {
                    SocketDataDispatcher(socket, bizMap)
                }
            }
        }

    }





    private fun getServerSocket(port: Int): ServerSocket {
        return try {
            ServerSocket(port)
        } catch (e: Exception) {
            e.printStackTrace()
            getServerSocket(port + 1)
        }
    }

    fun getPort() = server.localPort

    fun <T : BizRoute> addBizRoute(routeId: String, routeClass: Class<T>) {
        bizMap[routeId] = routeClass as Class<BizRoute>
    }

    fun destroy() {
        if (this::server.isInitialized) {
            server.close()
        }
    }


}