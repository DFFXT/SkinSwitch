package com.skin.skinswitch

import android.app.Application
import android.content.res.Resources
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skinswitch.const.AppConst

class App : Application() {
    override fun getResources(): Resources {
        return super.getResources()
    }

    override fun onCreate() {
        super.onCreate()

        SkinManager.init(
            this,
            object : DefaultProviderFactory() {
                // private val nightProvider = NightProvider(application)

                override fun getSkinName(theme: Int): String {
                    return "7"
                    if (theme == AppConst.THEME_NIGHT) {
                        return "7"
                        // return CustomSkinPathProvider(Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar")
                    }
                }

                override fun getSkinFolder(): String {
                    return getExternalFilesDir(null)?.absolutePath ?: filesDir.absolutePath
                }

                /*override fun getResourceProvider(ctx: Context, theme: Int): IResourceProvider {
                    val mode = if (theme == AppConst.THEME_NIGHT) {
                        Configuration.UI_MODE_NIGHT_YES
                    } else {
                        Configuration.UI_MODE_NIGHT_NO
                    }
                    val configuration = resources.configuration
                    configuration.uiMode = mode
                    resources.updateConfiguration(
                        configuration,
                        resources.displayMetrics
                    )
                    val color = getColor(R.color.main_background)
                    val night = color == 0xff000000.toInt()
                    val day = color == 0xffffffff.toInt()
                    return super.getResourceProvider(ctx, theme)
                }*/
            }
        )
        // SkinManager.switchTheme(1)
    }
}
