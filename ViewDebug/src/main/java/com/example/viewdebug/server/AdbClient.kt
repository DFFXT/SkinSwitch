package com.example.viewdebug.server

import com.example.viewdebug.server.route.BizRequest404Route
import com.example.viewdebug.server.route.BizRoute
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.readBytes
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
        private const val DEFAULT_CLIENT_PORT = 49872
    }

    private lateinit var client: ServerSocket

    private var buffer = ByteArray(1024)
    private var sockets: LinkedList<Socket> = LinkedList()

    private var heartBeatJob = false
    var isConnected = false
        private set(value) {
            field = value
            launch(Dispatchers.Main) {
                onConnectedListener?.onConnectedStateChanged(field)
            }
        }

    var onConnectedListener: ServerManager.OnConnectedListener? = null

    fun init() {
        client = getServerSocket(DEFAULT_CLIENT_PORT)
        launch(Dispatchers.IO) {
            Logger.d(TAG, "listen port: ${client.localPort}")
            while (true) {
                val socket = client.accept()
                if (!heartBeatJob) {
                    sendHeartbeat()
                    heartBeatJob = true
                }
                isConnected = true
                sockets.add(socket)

                Logger.d(TAG, "accept ${socket.remoteSocketAddress}")
            }
        }

    }

    private fun sendHeartbeat() {
        send("heart", "", callback = {
            isConnected = true
            Logger.v("AdbClient", "heart isConnected true")
            launch(Dispatchers.IO) {
                delay(3000)
                sendHeartbeat()
            }
        }, onError = {
            isConnected = false
            launch(Dispatchers.IO) {
                delay(3000)
                sendHeartbeat()
            }
            Logger.v("AdbClient", "heart isConnected false")
        })
    }

    fun send(cmd: String, content: String, callback: (String) -> Unit, onError: (() -> Unit)? = null) {
        launch(Dispatchers.IO) {
            synchronized(sockets) {
                val socket = sockets.lastOrNull()
                try {
                    socket?.let {
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
                    } ?: onError?.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        socket?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onError?.invoke()
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