package com.example.viewdebug.server

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
import kotlin.concurrent.thread

internal class AdbClient {
    companion object {
        private const val TAG = "AdbServer"

        // 默认端口
        private const val DEFAULT_SERVER_PORT = 49871
        private const val DEFAULT_CLIENT_PORT = 49872
    }

    private lateinit var client: ServerSocket

    private var buffer = ByteArray(1024)
    private var sockets: LinkedList<Socket> = LinkedList()

    fun init() {
        client = getServerSocket(DEFAULT_CLIENT_PORT)
        launch(Dispatchers.IO) {
            Logger.d(TAG, "listen port: ${client.localPort}")
            while (true) {
                val socket = client.accept()
                sockets.add(socket)
                Logger.d(TAG, "accept ${socket.remoteSocketAddress}")

                thread {
                    send("cmd", "content") {
                        Logger.d("ADBCLIENT", it)
                    }
                }
                /*launch(Dispatchers.IO) {
                    val input = DataInputStream(socket.getInputStream())
                    // id
                    val id = input.readInt()
                    // 指令长度
                    val cmdLength = input.readInt()
                    // 接收内容长度
                    val contentLength = input.readInt()
                    // 路由id
                    val routeId = String(input.readBytes(cmdLength))
                    val content = String(getBuffer(contentLength), 0, contentLength)
                    // 构建路由处理器
                    val routeClass = bizMap.getOrDefault(routeId, BizRequest404Route::class.java)
                    // 处理器处理对应请求
                    routeClass.newInstance().onRequest(routeId, content, ResponseWriterImpl(socket, id))
                }*/
            }
        }

    }

    fun send(cmd: String, content: String, callback: (String) -> Unit) {
        launch(Dispatchers.IO) {
            synchronized(sockets) {
                sockets.lastOrNull()?.let {
                    val out = DataOutputStream(it.getOutputStream())
                    val cmdBytes = cmd.toByteArray()
                    val contentBytes = content.toByteArray()
                    out.writeInt(cmdBytes.size)
                    out.writeInt(contentBytes.size)
                    out.write(cmdBytes)
                    out.write(contentBytes)

                    val input = DataInputStream(it.getInputStream())
                    val responseLength = input.readInt()
                    val buffer = getBuffer(responseLength)
                    input.readBulk(buffer, 0, responseLength)
                    val responseStr = String(buffer, 0, responseLength)
                    callback(responseStr)
                }
            }

        }

    }

    private fun getBuffer(length: Int): ByteArray {
        if (buffer.size < length) {
            buffer = ByteArray(length)
        }
        return buffer
    }

    private fun InputStream.readBulk(buffer: ByteArray, offset: Int, length: Int) {
        var readSize = 0
        while (readSize < length) {
            val read = this.read(buffer, offset, length)
            if (read > 0) {
                readSize += read
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

    fun getPort() = client.localPort

    fun destroy() {
        if (this::client.isInitialized) {
            client.close()
        }
    }


}