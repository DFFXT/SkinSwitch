package com.example.viewdebug.server

import com.example.viewdebug.util.readBytes
import java.io.DataInputStream
import java.io.InputStream

class RequestImpl(private val inputStream: DataInputStream) : Request {
    private val length: Int = inputStream.readInt()

    override fun getContentLength(): Int = length

    override fun getContent(): ByteArray {
        return inputStream.readBytes(length)
    }

    override fun getContentStream(): InputStream = inputStream
}