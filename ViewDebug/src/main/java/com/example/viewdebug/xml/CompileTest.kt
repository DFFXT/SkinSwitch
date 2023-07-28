package com.example.viewdebug.xml

import android.content.pm.PackageManager
import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.image.XmlParser
import com.example.viewdebug.xml.pack.PackAssetsFile
import com.example.viewdebug.xml.struct.XmlCompiler
import com.example.viewdebug.xml.struct.reader.ChunkFile
import com.example.viewdebug.xml.struct.writer.helper.ExternalFunction
import com.example.viewdebug.xml.struct.writer.helper.value.AttrDimensionValueCompile
import com.skin.log.Logger
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.FileSystems

object CompileTest {

    @JvmStatic
    fun main(vararg args: String) {
        val str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><TextView xmlns:android=\"http://schemas.android.com/apk/res/android\" android:layout_width=\"match_parent\" android:text=\"11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\" android:layout_height=\"wrap_content\"></TextView>"

        val compiler = XmlCompiler(ViewDebugInitializer.ctx)
        val buffer = compiler.compile(str.byteInputStream())
        val reader = ChunkFile()
        //reader.read(buffer)
        Logger.i("CompileTest", reader.toString())


        val p = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + "/test_write.xml"
        val arr = ByteArray(buffer.limit())
        buffer.position(0)
        buffer.get(arr, 0, arr.size)
        File(p).writeBytes(arr)


        val stdFile = ChunkFile()
        val b = ViewDebugInitializer.ctx.assets.open("view_debug_compile_test.xml").readBytes()
        val bf = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN)
        stdFile.read(bf)
        val t = ViewDebugInitializer.ctx.assets.openXmlResourceParser("assets/test_write.xml")
        val x = XmlParser().getXml(ViewDebugInitializer.ctx, t) {}

        val t1 = ViewDebugInitializer.ctx.assets.openXmlResourceParser("assets/view_debug_compile_test.xml")
        val x1 = XmlParser().getXml(ViewDebugInitializer.ctx, t1) {}
        /**
         * //12582945 shr 8 shl 8
        // 1-4位确定类型
        // 5-6位确定基数
        // 8-32确定系数
        val t = data << 8 // // 移位结果
        val result = t/radix // 系数
        判断系数是否可以放在8-32之间
        是
        系数写入8-32,5-6写入基数0,1-4写入单位
        否
         */

        Thread {
            val pm = ViewDebugInitializer.ctx.packageManager
            val pack = PackAssetsFile(ViewDebugInitializer.ctx)
            val p = ViewDebugInitializer.ctx.externalCacheDir!!.absolutePath + "/222.apk"
            val info = pm.getPackageArchiveInfo(pack.getPackedApkPath(), PackageManager.GET_ACTIVITIES)
            val f = 0
           // pack.addLayoutFile("ssssss".byteInputStream(), "xxx")
            //pack.pack()
        }.start()
    }

    private fun readStd() {

    }
}
