package com.skin.skinswitch.skin

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import com.skin.skincore.provider.IResourceProvider

class NightProvider(private val application: Application) : IResourceProvider {
    private val nightContext: Context by lazy {
        val configuration = application.resources.configuration
        configuration.uiMode = Configuration.UI_MODE_NIGHT_YES
        application.resources.updateConfiguration(
            configuration,
            application.resources.displayMetrics
        )
        return@lazy application
    }

    init {
        nightContext
    }
    override fun getColor(resId: Int?): Int? {
        resId ?: return null
        return application.getColor(resId)
    }

    override fun getStateColor(resId: Int?): ColorStateList? {
        resId ?: return null
        return application.getColorStateList(resId)
    }

    override fun getDrawable(resId: Int?): Drawable? {
        resId ?: return null
        return application.getDrawable(resId)
    }
}
