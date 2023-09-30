package com.example.viewdebug.server

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.server.route.BizRequestRClassRoute
import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import java.io.InputStream
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
internal class HttpServer {
    companion object {
        private const val TAG = "AdbServer"
    }

    private lateinit var server: ServerSocket
    private val bizMap = HashMap<String, Class<BizRoute>>()

    fun init(port: Int) {
        val rClass = Class.forName(ViewDebugInitializer.ctx.packageName + ".R")







        // BizRequestRClassRoute.createRFile()



        launch(Dispatchers.IO) {
            try {
                server = ServerSocket(port)
                Logger.d(TAG, "listen: $port")
                while (true) {
                    try {
                        val socket = server.accept()
                        Logger.d(TAG, "accept ${socket.remoteSocketAddress}")
                        launch(Dispatchers.IO) {
                            val input = socket.getInputStream()
                            val b = ByteArray(1024)
                            val builder = ByteArrayOutputStream()
                            while (true) {
                                val len = input.read(b)
                                if (len != -1) {
                                    builder.write(b, 0, len)
                                    Logger.d(TAG, String((builder.toByteArray())))
                                } else {
                                    break
                                }
                            }


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    fun <T : BizRoute> addBizRoute(routeId: String, routeClass: Class<T>) {
        bizMap[routeId] = routeClass as Class<BizRoute>
    }

    fun destroy() {
        if (this::server.isInitialized) {
            server.close()
        }
    }


    fun InputStream.readBytes(length: Int): ByteArray {
        val arr = ByteArray(length)
        var temp = ByteArray(length)
        var recSize = 0
        while (true) {
            val len = this.read(temp)
            if (len == -1) break
            System.arraycopy(temp, 0, arr, recSize, len)
            temp = ByteArray(length - len)
            recSize += len
            if (recSize == length) break
        }
        return arr
    }
}