package com.skin.skinswitch

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import com.CustomViewTestSkinView
import com.cneeds.multipixelsuiadapter.MultiPixelsAdjust
import com.cneeds.multipixelsuiadapter.MultiPixelsAdjustManager
import com.example.skinswitch.R
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.AttrParseListener
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

        SkinManager.addAttributeCollection(object : BaseViewApply<CustomViewTestSkinView>(R.attr.custom_bg) {
            override fun apply(view: CustomViewTestSkinView, resId: Int, resType: String, provider: IResourceProvider, theme: Resources.Theme?) {
                // 当View创建时，会立即执行
                view.setCustomBg(provider.getDrawable(resId, theme))
            }
        })
        SkinManager.addViewAttrParseListener(object :AttrParseListener {
            override fun onAttrParsed(parent: View?, view: View, attributeSet: AttributeSet, union: ViewUnion) {
                val f= 0
            }

            override fun onInflateFinish(rootView: View) {
                super.onInflateFinish(rootView)
            }
        })

        MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1280, 720), "")
        MultiPixelsAdjustManager.addMultiPixelsSuffix(MultiPixelsAdjust.UIConfig(1080, 1920), "_vertical")
        MultiPixelsAdjustManager.recalculate()

        //SkinManager.switchTheme(AppConst.THEME_CARTOON)
    }
}
