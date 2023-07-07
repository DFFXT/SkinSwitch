package com.example.viewdebug.xml

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.reader.ChunkFile
import com.skin.log.Logger
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.ByteBuffer

object CompileTest {

    @JvmStatic
    fun main(vararg args: String) {
        val str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<androidx.constraintlayout.widget.ConstraintLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"wrap_content\">\n" +
                "\n" +
                "</androidx.constraintlayout.widget.ConstraintLayout>"

        val compiler = XmlCompiler(ViewDebugInitializer.ctx)
        val buffer = compiler.compile(str.byteInputStream())
        val reader = ChunkFile()
        reader.read(buffer)
        Logger.i("CompileTest", reader.toString())


        val p = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + "/test_.xml"
        val arr = ByteArray(buffer.limit())
        buffer.position(0)
        buffer.get(arr, 0, arr.size)
        File(p).writeBytes(arr)
    }
}
