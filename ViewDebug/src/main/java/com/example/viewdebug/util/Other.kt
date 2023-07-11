package com.example.viewdebug.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.XmlParser
import com.example.viewdebug.ui.image.XmlTextDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
internal fun tryShowXmlText(ctx: Context, id: Int, hostPage: UIPage) {
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
        dialog.show(id, parsedValue)
    } catch (e: Exception) {
        copyToClipboard(ctx, attrValue)
    }
}

internal val applicationJob = SupervisorJob()
internal val applicationScope = CoroutineScope(applicationJob)

internal fun launch(context: CoroutineContext = EmptyCoroutineContext,
                    start: CoroutineStart = CoroutineStart.DEFAULT,
                    block: suspend CoroutineScope.() -> Unit) {
    applicationScope.launch(context, start, block)
}


internal fun String.shortToast() {
    Toast.makeText(ViewDebugInitializer.ctx, this, Toast.LENGTH_SHORT).show()
}
