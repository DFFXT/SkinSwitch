package com.skin.skincore.plug

import android.content.Context
import android.os.SystemClock
import com.skin.log.Logger
import com.skin.skincore.provider.ResourcesProviderManager
import java.io.File
import java.io.FileOutputStream

/**
 * 快速安装开发皮肤包
 * 这个功能仅推荐在开发模式下使用，因为每次启动都会进行皮肤包拷贝，如果皮肤包过多或者过大影响启动速度
 */
@Deprecated("never use")
object SkinPackDeveloping {
    /**
     * asset目录皮肤包路径
     */
    var SKIN_DEVELOPING_FOLDER = "developing_skin"

    /**
     * 将生成的皮肤包拷贝到设置的皮肤包目录
     */
    fun sinkPackInstall(ctx: Context) {
        try {
            val startTime = SystemClock.elapsedRealtime()
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
                    Logger.d("SkinPackDeveloping", "install sink pack:$fileName")
                }
                Logger.e(
                    "SkinPackDeveloping",
                    "install skin pack finish, use time: ${SystemClock.elapsedRealtime() - startTime}"
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            Logger.e("SkinPackDeveloping", "install skin pack failed")
        }
    }
}
