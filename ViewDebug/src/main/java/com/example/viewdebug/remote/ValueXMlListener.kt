package com.example.viewdebug.remote

import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import kotlinx.coroutines.Dispatchers
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * values类型的xml监听
 * values类型和xml不一样
 * 只能存在一个values xml文件，有新增时，就将新旧文件进行合并
 */
@Deprecated("目前无法实现该功能，无法拦截color、string以及drawable的加载")
internal class ValueXMlListener : RemoteFileReceiver.FileWatcher(TYPE_VALUES_XML, consume = true) {
    override fun onReceive(fileContainer: FileContainer) {
        return
        fileContainer.fileInfo.forEach {
            val file = File(it.path)
            if (file.exists()) {
                val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                parse(doc)
            }
        }
        launch(Dispatchers.Main) {
            WindowControlManager.refreshModifyListPage()
            WindowControlManager.notifyModifyList()
        }
    }

    private fun parse(node: Node) {
        if (node.nodeType == Node.ELEMENT_NODE) {
            try {
                val nodeName = node.nodeName
                // 目前接受这些类型数据
                if (nodeName == "string" || nodeName == "color" || nodeName == "dimen") {
                    val nameAttrValue = node.attributes.getNamedItem("name").nodeValue
                    val id = ViewDebugInitializer.ctx.resources.getIdentifier(
                        nameAttrValue,
                        nodeName,
                        ViewDebugInitializer.ctx.packageName
                    )
                    val valueNode = node.childNodes.item(0)
                    if (valueNode.nodeType == Node.TEXT_NODE) {
                        ViewDebugResourceManager.addValuesInterceptor(id, valueNode.nodeValue)
                    }
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val size = node.childNodes.length
        for (i in 0 until size) {
            parse(node.childNodes.item(i))
        }
    }
}