package com.cneeds.multipixelsuiadapter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import java.util.Collections

/**
 * 多分辨率适配入口
 */

class MultiPixelsInitializer: Initializer<MultiPixelsInitializer> {
    override fun create(context: Context): MultiPixelsInitializer {
        MultiPixelsAdjustManager.init(context.applicationContext as Application)
        MultiPixelsAdjustManager.makeContextMultiPixelsAble(context)
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
            }

        })

        Log.d("MultiPixelsInitializer", "create: MultiPixelsInitializer")
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}