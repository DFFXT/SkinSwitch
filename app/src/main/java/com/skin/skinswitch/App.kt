package com.skin.skinswitch

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.CustomViewTestSkinView
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.Asset
import com.skin.skincore.asset.AssetLoaderManager
import com.skin.skincore.asset.IAsset
import com.skin.skincore.asset.IAssetFactory
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.provider.MergeResource
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
        AssetLoaderManager.setAssetFactory(object : IAssetFactory {
            override fun createAsset(context: Context, skinPathProvider: ISkinPathProvider): IAsset {
                return object : Asset(context, skinPathProvider) {
                    override fun getResource(): Resources {
                        val res = context.resources
                        if (res is MergeResource) {
                            return res.default
                        }
                        return super.getResource()
                    }
                }
            }

        })
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
