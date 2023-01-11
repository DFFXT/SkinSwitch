package com.skin.skinswitch

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Environment
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.CustomSkinPathProvider
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ISkinPathProvider
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

                override fun getPathProvider(theme: Int): ISkinPathProvider? {
                    if (theme == AppConst.THEME_NIGHT) {
                        return CustomSkinPathProvider(Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar")
                    }
                    return null
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
        //SkinManager.switchTheme(1)
    }
}
