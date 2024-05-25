package com.skin.skinswitch

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.CustomViewTestSkinView
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.MergeResource
import com.skin.skinswitch.const.AppConst
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener

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

                override fun getSkinName(context: Context, theme: Int): String {
                    if (theme == AppConst.THEME_CARTOON) {
                        return "7"
                        // return CustomSkinPathProvider(Environment.getExternalStorageDirectory().absolutePath + "/skinPack-cartoon-debug - 副本.rar")
                    }
                    return ""
                }

                override fun getSkinFolder(): String {
                    return getExternalFilesDir(null)?.absolutePath ?: filesDir.absolutePath
                }

                override fun getResourceProviderKey(ctx: Context, theme: Int): String {
                    return super.getResourceProviderKey(ctx, theme) + ctx.hashCode()
                }

                override fun differentContextWithDifferentProvider(): Boolean {
                    return true
                }
            }
        )
        AutoSizeConfig.getInstance().setOnAdaptListener(object : onAdaptListener {
            override fun onAdaptBefore(target: Any?, activity: Activity?) {

            }

            override fun onAdaptAfter(target: Any?, activity: Activity) {
                val c = activity.resources.configuration
                val dm = activity.resources.displayMetrics
                if (c.densityDpi != dm.densityDpi) {
                    c.densityDpi = dm.densityDpi
                    activity.resources.updateConfiguration(c, dm)
                }
                // activity?.resources?.updateMergeResourceDisplayMetrics()
            }

        })

        SkinManager.addAttributeCollection(object : BaseViewApply<CustomViewTestSkinView>(R.attr.custom_bg) {
            override fun apply(view: CustomViewTestSkinView, resId: Int, resType: String, provider: IResourceProvider, theme: Resources.Theme?) {
                // 当View创建时，会立即执行
                view.setCustomBg(provider.getDrawable(resId, theme))
            }
        })

        //SkinManager.switchTheme(AppConst.THEME_CARTOON)
    }
}
