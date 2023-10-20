package com.example.viewdebug.remote

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.relaunchApp
import com.example.viewdebug.util.shortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.LinkedList

/**
 * 拦截xml编译xml，目前支持drawable、layout
 */
internal class DefaultXmlFileListener : RemoteFileReceiver.FileWatcher(
    TYPE_LAYOUT,
    TYPE_COLOR,
    TYPE_DRAWABLE,
    consume = true
) {

    override fun onReceive(fileContainer: FileContainer) {

        val handleList = fileContainer.fileInfo
        val ctx = ViewDebugInitializer.ctx
        try {
            val layoutId = LinkedList<Int>()
            handleList.forEach {
                val file = File(it.path)
                if (file.exists()) {
                    val id = ctx.resources.getIdentifier(
                        file.nameWithoutExtension,
                        it.type,
                        ctx.packageName
                    )
                    val type = ctx.resources.getResourceTypeName(id)
                    XmlLoadManager.compileXml(ctx, file.inputStream(), id, type)
                    layoutId.add(id)
                }
            }
            XmlLoadManager.compileApk()
            XmlLoadManager.loadApk()
            launch {
                withContext(Dispatchers.Main) {
                    // 全局刷新资源
                    layoutId.forEach { id ->
                        XmlLoadManager.applyGlobalViewByResId(id)
                    }
                    ctx.getString(R.string.view_debug_file_receive_xml_ok_tip).shortToast()
                    WindowControlManager.notifyModifyList()
                    if (fileContainer.reboot) {
                        relaunchApp(ctx, true)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            launch {
                withContext(Dispatchers.Main) {
                    ctx.getString(R.string.view_debug_file_receive_xml_err_tip).shortToast()
                }
            }

        }
    }


}