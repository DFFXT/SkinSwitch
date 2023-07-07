package com.example.viewdebug.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.viewdebug.ui.UIPage
import com.example.viewdebug.ui.image.XmlParser
import com.example.viewdebug.ui.image.XmlTextDialog

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

