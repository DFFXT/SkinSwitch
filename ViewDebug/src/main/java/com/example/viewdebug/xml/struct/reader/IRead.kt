package com.example.viewdebug.xml.struct.reader

import java.nio.ByteBuffer

interface IRead {
    fun read(data: ByteBuffer)
}

interface IWrite {
    fun write(data: ByteBuffer)
}