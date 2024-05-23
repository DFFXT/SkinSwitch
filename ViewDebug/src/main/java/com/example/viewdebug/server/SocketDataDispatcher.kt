package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRequest404Route
import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import kotlinx.coroutines.Dispatchers
import java.io.DataInputStream
import java.io.InputStream
import java.net.Socket

internal class SocketDataDispatcher(private val socket: Socket, private val bizMap: HashMap<String, Class<BizRoute>>) {
    private var buffer = ByteArray(1024)
    init {

        try {
            val input = DataInputStream(socket.getInputStream())
            // 指令长度
            val cmdLength = input.readInt()
            // 接收内容长度
            val contentLength = input.readInt()
            // 路由id
            val routeId = input.readFullSizeString(buffer, cmdLength)
            val content = input.readFullSizeString(buffer, contentLength)
            // 构建路由处理器
            val routeClass = bizMap.getOrDefault(routeId, BizRequest404Route::class.java)
            // 处理器处理对应请求
            routeClass.newInstance().onRequest(routeId, content, ResponseWriterImpl(socket))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun InputStream.readFullSizeString(buffer: ByteArray, length: Int): String {
        var b = buffer
        if (b.size < length) {
            b = ByteArray(length)
        }
        return String(readBulk(b, 0, length), 0, length)
    }

    private fun InputStream.readBulk(buffer: ByteArray, offset: Int, length: Int): ByteArray {
        var readSize = 0
        while (readSize < length) {
            val read = this.read(buffer, offset, length)
            if (read > 0) {
                readSize += read
            } else {
                Thread.sleep(50)
            }
        }
        return buffer
    }
}