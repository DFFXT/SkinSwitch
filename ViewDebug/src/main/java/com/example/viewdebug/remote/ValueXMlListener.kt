package com.example.viewdebug.remote

import com.example.viewdebug.R
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.xml.XmlLoadManager
import com.example.viewdebug.ui.WindowControlManager
import com.example.viewdebug.ui.skin.ViewDebugResourceManager
import com.example.viewdebug.util.launch
import com.example.viewdebug.util.shortToast
import kotlinx.coroutines.Dispatchers
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

/**
 * values类型的xml监听
 * values类型和xml不一样
 * 只能存在一个values xml文件，有新增时，就将新旧文件进行合并
 */
internal class ValueXMlListener : RemoteFileReceiver.FileWatcher {
    override fun onChange(fileInfo: RemoteFileReceiver.FileWatcher.FileInfo): Boolean {
        if (fileInfo.type == RemoteFileReceiver.FileWatcher.TYPE_VALUES_XML) {
            launch(Dispatchers.IO) {
                try {
                    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse("file:///" + fileInfo.path)
                    val size = doc.childNodes.length
                    for (i in 0 until size) {
                        parse(doc.childNodes.item(i))
                    }
                    XmlLoadManager.saveValues()
                    launch(Dispatchers.Main) {
                        WindowControlManager.refreshModifyListPage()
                        WindowControlManager.notifyModifyList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    launch(Dispatchers.Main) {
                        ViewDebugInitializer.ctx.getString(R.string.view_debug_value_xml_parse_error)
                            .shortToast()
                    }
                }
            }
            return true
        }
        return false
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