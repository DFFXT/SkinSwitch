package com.skin.skinswitch

import androidx.annotation.Keep
import com.example.skinswitch.BuildConfig
import com.example.viewdebug.ViewDebugInitializer
import com.example.viewdebug.apply.IBuildIdentification

@Keep
class ViewDebugStarter : ViewDebugInitializer() {
    override fun getBuildIdentification(): IBuildIdentification? {
        return object : IBuildIdentification {
            override fun getBuildId(): String {
                return BuildConfig.buildTime.toString()
            }
        }
    }
}
