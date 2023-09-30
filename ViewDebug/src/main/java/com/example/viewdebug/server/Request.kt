package com.example.viewdebug.server

import java.io.InputStream

interface Request {
    fun getContentLength(): Int
    fun getContent(): ByteArray

    fun getContentStream(): InputStream
}