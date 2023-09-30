package com.example.viewdebug.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.page.XmlParser
import com.example.viewdebug.ui.page.XmlTextDialog
import com.fxf.debugwindowlibaray.ui.UIPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 复制到剪切板
 */
internal fun copyToClipboard(ctx: Context, text: String) {
    val clipboardManager = ctx.getSystemService(ClipboardManager::class.java)
    clipboardManager.setPrimaryClip(ClipData.newPlainText("UI调试", text))
}

/**
 * 显示xml弹窗
 */
internal fun tryShowXmlText(ctx: Context, id: Int, hostPage: UIPage, attributeId: Int?, target: WeakReference<View>?): Boolean {
    val attrValue = ctx.resources.getResourceEntryName(id)
    try {
        val parsedValue = XmlParser().getXmlText(ctx, id) { text ->
            // 只复制名称，不复制类型
            if (text.indexOf("/") >= 0) {
                copyToClipboard(ctx, text.split('/')[1])
            } else {
                copyToClipboard(ctx, text)
            }
        }
        val dialog = XmlTextDialog(ctx, hostPage)
        dialog.show(id, parsedValue, attributeId, target)
        return true
    } catch (e: Exception) {
        copyToClipboard(ctx, attrValue)
    }
    return false
}

internal val applicationJob = SupervisorJob()
internal val applicationScope = CoroutineScope(applicationJob)

internal fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    applicationScope.launch(context, start, block)
}


internal fun String.shortToast() {
    Toast.makeText(ViewDebugInitializer.ctx, this, Toast.LENGTH_SHORT).show()
}

internal fun String.makeAsDir(): File {
    val dir = File(this)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}



fun InputStream.readBytes(length: Int): ByteArray {
    val arr = ByteArray(length)
    var temp = ByteArray(length)
    var recSize = 0
    while (true) {
        val len = this.read(temp)
        if (len == -1) break
        System.arraycopy(temp, 0, arr, recSize, len)
        temp = ByteArray(length - len)
        recSize += len
        if (recSize == length) break
    }
    return arr
}