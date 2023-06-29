package com.example.viewdebug

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import java.util.Collections

/**
 * 自动初始化
 */
@Keep
class ViewDebugInitializer : Initializer<ViewDebugInitializer> {
    override fun create(context: Context): ViewDebugInitializer {
        ViewDebugManager.init(context.applicationContext as Application)
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}
