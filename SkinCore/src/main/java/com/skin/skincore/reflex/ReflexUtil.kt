package com.skin.skincore.reflex

import android.content.Context
import android.content.ContextWrapper
import android.view.ContextThemeWrapper

val contextBaseFiled by lazy {
    ContextWrapper::class.java.getDeclaredField("mBase").apply {
        isAccessible = true
    }
}

val inflater by lazy {
    ContextThemeWrapper::class.java.getDeclaredField("mInflater").apply {
        isAccessible = true
    }
}

fun Context.switchBaseContext(base: Context) {
    contextBaseFiled.set(this, base)
}
