package com.example.viewdebug.xml.pack

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object IOUtil {
    /**
     * 将文件压缩为zip包
     * @param file 如果是文件夹，则只对文件夹内的文件进行压缩（不生成当前文件夹名称），如果是文件，则直接压缩
     */
    fun zip(output: String, file: File) {
        if (!file.exists()) return
        FileOutputStream(output).use {
            ZipOutputStream(it).use { zip ->
                if (file.isDirectory) {
                    file.listFiles()?.forEach { f ->
                        zipFile(zip, f, "", ByteArray(1024 * 1024))
                    }
                } else {
                    zipFile(zip, file, "",  ByteArray(1024 * 1024))
                }
            }
        }
    }

    /**
     * @param byteArray 复用的容器，不用静态变量的原因是线程安全问题
     */
    private fun zipFile(zip: ZipOutputStream, file: File, entryName: String, byteArray: ByteArray) {
        if (file.isDirectory) {
            val nextName = if (entryName.isNotEmpty()) {
                entryName + File.separator + file.name
            } else {
                file.name
            }
            zip.putNextEntry(ZipEntry("$nextName/"))
            file.listFiles()?.forEach {
                zipFile(zip, it, nextName, byteArray)
            }
        } else {
            val name = if (entryName.isNotEmpty()) {
                entryName + File.separator + file.name
            } else {
                file.name
            }
            zip.putNextEntry(ZipEntry(name))
            copy(FileInputStream(file), zip, byteArray)
        }
    }

    fun copy(inputStream: InputStream, outputStream: OutputStream, byteArray: ByteArray) {
        inputStream.use {
            var len: Int
            while (true) {
                len = it.read(byteArray, 0, byteArray.size)
                if (len != -1) {
                    outputStream.write(byteArray, 0, len)
                } else {
                    break
                }
            }
        }
    }
}