package com.example.viewdebug.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.server.RemoteControl
import com.example.viewdebug.server.ServerManager
import com.example.viewdebug.ui.page.XmlParser
import com.example.viewdebug.ui.page.XmlTextDialog
import com.fxf.debugwindowlibaray.ui.UIPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.exitProcess

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
): Job {
    return applicationScope.launch(context, start, block)
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

/**
 * @param type [RemoteControl.TYPE_XMl][RemoteControl.TYPE_CLASS]
 */
internal fun copyOrJump(name: String, type: String) {
    if (ServerManager.isConnected()) {
        RemoteControl.openFile(name, type)
    } else {
        copyToClipboard(ViewDebugInitializer.ctx, name)
    }
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

private fun getLauncherActivity(ctx: Context, pkg: String): String? {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.setPackage(pkg)
    val pm: PackageManager = ctx.packageManager
    val info = pm.queryIntentActivities(intent, 0)
    return if (info.size == 0) {
        ""
    } else info[0].activityInfo.name
}

private fun getLaunchAppIntent(ctx: Context): Intent? {
    val pkgName = ctx.packageName
    val launcherActivity: String = getLauncherActivity(ctx, pkgName) ?: return null
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.setClassName(pkgName, launcherActivity)
    return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

/**
 * 重启进程
 */
fun relaunchApp(ctx: Context, isKillProcess: Boolean) {
    val intent: Intent? = getLaunchAppIntent(ctx)
    if (intent == null) {
        Log.e("AppUtils", "Didn't exist launcher activity.")
        return
    }
    intent.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    )
    ctx.startActivity(intent)
    if (!isKillProcess) return
    Process.killProcess(Process.myPid())
    exitProcess(0)
}