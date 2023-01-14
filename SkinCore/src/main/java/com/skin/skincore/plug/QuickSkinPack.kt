package com.skin.skincore.plug

import android.content.Context
import com.skin.log.Logger
import com.skin.skincore.provider.ResourcesProviderManager
import java.io.File
import java.io.FileOutputStream

/**
 * 快速安装开发皮肤包
 */
object QuickSkinPack {
    fun sinkPackInstall(ctx: Context) {
        var SKIN_DEVELOPING_FOLDER = "developing_skin"
        val className = ctx.packageName + "BuildConfig"
        val skinDeveloping =
            Class.forName(ctx.packageName + ".BuildConfig").getDeclaredField("SKIN_DEVELOP")
                .get(null)
        if (skinDeveloping == true) {
            // 皮肤开发中，直接覆盖
            val dir = File(ResourcesProviderManager.getSkinFolder())
            if (!dir.exists()) {
                dir.mkdirs()
            }
            ctx.assets.list(SKIN_DEVELOPING_FOLDER)?.forEach { fileName ->
                FileOutputStream(ResourcesProviderManager.getSkinFolder() + '/' + fileName).use {
                    ctx.assets.open("$SKIN_DEVELOPING_FOLDER/$fileName").copyTo(it)
                    it.flush()
                }
                Logger.d("QuickSkinPack", "install sink pack:$fileName")
            }
        }
    }
}
