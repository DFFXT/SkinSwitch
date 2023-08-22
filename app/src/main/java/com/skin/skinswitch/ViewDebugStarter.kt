package com.skin.skinswitch

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import com.example.skinswitch.BuildConfig
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.dex.DexLoadManager
import com.example.viewdebug.dex.IBuildIdentification
import java.util.Collections


@Keep
class ViewDebugStarter : Initializer<ViewDebugStarter> {
    override fun create(context: Context): ViewDebugStarter {
        // dex设置版本接口，buildTime在gradle中赋值，必须在ViewDebugInitializer初始化之前赋值
        DexLoadManager.setBuildIdentification(object : IBuildIdentification {
            override fun getBuildId(): String {
                return BuildConfig.buildTime.toString()
            }
        })
        AppInitializer.getInstance(context).initializeComponent(ViewDebugInitializer::class.java)
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}
