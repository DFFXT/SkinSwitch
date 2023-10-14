package com.example.viewdebug.remote

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import com.skin.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 拦截xml编译xml，目前支持drawable、layout
 */
internal class DefaultXmlFileListener : RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        val path = fileInfo.path
        val type = fileInfo.type
        if (path.endsWith(".xml")) {
            val ctx = ViewDebugInitializer.ctx
            val file = File(path)
            when (type) {
                RemoteFileReceiver.FileWatcher.TYPE_LAYOUT,
                RemoteFileReceiver.FileWatcher.TYPE_COLOR,
                RemoteFileReceiver.FileWatcher.TYPE_DRAWABLE -> {
                    launch(Dispatchers.IO) {
                        val id = ctx.resources.getIdentifier(file.nameWithoutExtension, type, ctx.packageName)
                        Logger.i("DefaultXmlFileListener", "$path $type $id")
                        if (id != 0) {
                            if (XmlLoadManager.compileXmlAndApply(ctx, file.inputStream(), id, type)) {
                                withContext(Dispatchers.Main) {
                                    // 全局刷新资源
                                    if(type != RemoteFileReceiver.FileWatcher.TYPE_LAYOUT) {
                                        XmlLoadManager.applyGlobalViewByResId(id)
                                    }
                                    ctx.getString(R.string.view_debug_file_receive_xml_ok_tip, file.name).shortToast()
                                    WindowControlManager.notifyModifyList()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    ctx.getString(R.string.view_debug_file_receive_xml_err_tip, file.name).shortToast()
                                }
                            }
                        }
                    }
                    return true
                }
            }

        }
        return false
    }


}