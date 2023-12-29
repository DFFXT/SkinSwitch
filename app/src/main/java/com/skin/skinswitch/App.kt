package com.skin.skinswitch

import android.app.Application
import android.content.res.Resources
import com.CustomViewTestSkinView
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skinswitch.const.AppConst

class App : Application() {
    override fun getResources(): Resources {
        return super.getResources()
    }

    override fun onCreate() {
        super.onCreate()


        initSkin()
    }

    private fun initSkin() {
        SkinManager.init(
            this, R.style.Theme_SkinSwitch,
            object : DefaultProviderFactory() {
                // private val nightProvider = NightProvider(application)

                override fun getSkinName(theme: Int): String {
                    if (theme == AppConst.THEME_CARTOON) {
                        return "7"
                        // return CustomSkinPathProvider(Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar")
                    }
                    return ""
                }

                override fun getSkinFolder(): String {
                    return getExternalFilesDir(null)?.absolutePath ?: filesDir.absolutePath
                }
            }
        )

        SkinManager.addAttributeCollection(object : BaseViewApply<CustomViewTestSkinView>(R.attr.custom_bg) {
            override fun apply(view: CustomViewTestSkinView, resId: Int, resType: String, provider: IResourceProvider, theme: Resources.Theme?) {
                // 当View创建时，会立即执行
                view.setCustomBg(provider.getDrawable(resId, theme))
            }
        })

        //SkinManager.switchTheme(AppConst.THEME_CARTOON)
    }
}
