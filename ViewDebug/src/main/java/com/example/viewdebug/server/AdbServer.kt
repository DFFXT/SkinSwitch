package com.example.viewdebug.server

import android.telecom.Call
import com.example.viewdebug.server.route.BizRequest404Route
import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.readBytes
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger

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

    private var buffer = ByteArray(1024)
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
                    val input = DataInputStream(socket.getInputStream())
                    // 指令长度
                    val cmdLength = input.readInt()
                    // 接收内容长度
                    val contentLength = input.readInt()
                    // 路由id
                    val routeId = String(input.readBytes(cmdLength))
                    val content = input.readFullSizeString(contentLength)
                    // 构建路由处理器
                    val routeClass = bizMap.getOrDefault(routeId, BizRequest404Route::class.java)
                    // 处理器处理对应请求
                    routeClass.newInstance().onRequest(routeId, content, ResponseWriterImpl(socket))
                }
            }
        }

    }

    private fun InputStream.readFullSizeString(length: Int): String {
        if (buffer.size < length) {
            buffer = ByteArray(length)
        }
        return String(readBulk(buffer, 0, length), 0, length)
    }

    private fun InputStream.readBulk(buffer: ByteArray, offset: Int, length: Int): ByteArray {
        var readSize = 0
        while (readSize < length) {
            val read = this.read(buffer, offset, length)
            if (read > 0) {
                readSize += read
            }
        }
        return buffer
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