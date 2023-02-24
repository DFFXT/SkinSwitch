package com.skin.skinswitch

import android.app.Application
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.AttrSrcApply
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
            this,
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
        SkinManager.addAttributeCollection(object : BaseViewApply<View>(android.R.attr.layout_width, eventType = 898) {
            override fun apply(view: View, resId: Int, resType: String, provider: IResourceProvider, theme: Resources.Theme?) {
                (view.layoutParams as ViewGroup.LayoutParams).apply {
                    width = provider.getCurrentResource().getDimension(resId).toInt()
                    view.layoutParams = this
                }
            }
        })

        //SkinManager.switchTheme(AppConst.THEME_CARTOON)
    }
}
