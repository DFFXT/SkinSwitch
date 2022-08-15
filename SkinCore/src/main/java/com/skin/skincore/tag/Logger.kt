package com.skin.skincore.tag

import android.util.Log

object Logger {
    @JvmStatic
    fun logI(tag: String, msg: String?) {
        Log.i(tag, msg ?: "")
    }
}