package com.example.viewdebug.server.route

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.server.Request
import com.example.viewdebug.server.ResponseWriter
import java.io.File

/**
 * 请求R文件
 * 返回body格式
 * int 长度
 * 文件内容
 * int长度
 * 文件内容
 */
internal class BizRequestRClassRoute : BizRoute {
    private val rPath = ViewDebugInitializer.ctx.cacheDir.absolutePath + File.pathSeparator + "R.java"
    override fun onRequest(routeId: String, request: Request, response: ResponseWriter) {
        val file = File(rPath)
        androidx.core.R.layout.notification_action
        R.layout.view_debug_dialog_detail_info
        val content = String(request.getContent())
        val responseContent = content.split("\n").map {
            createRFile(it).toByteArray()
        }
        // +4 算上长度标识
        response.writeContentLength(responseContent.sumOf { it.size + 4 })
        responseContent.forEach {
            response.writeInt(it.size)
            response.write(it)
        }
    }

    companion object {
        fun createRFile(pkgName: String):String {
            val rClass = Class.forName("$pkgName.R")
            val builder = StringBuilder()
            builder.append("package $pkgName;")
            builder.append("public class R {\n")
            rClass.classes.forEach {
                builder.append("public static class ${it.simpleName} {\n")
                it.declaredFields.forEach {
                    if (it.type.isArray) {
                        val data = (it.get(null) as IntArray).joinToString(",")
                        builder.append("public final static int[] ${it.name} = {$data};\n")
                    } else {
                        builder.append("public final static int ${it.name} = ${it.get(null)};\n")
                    }
                }
                builder.append("\n}")
            }
            builder.append("\n}")
            return builder.toString()
        }
    }

}