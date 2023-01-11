package com.skin.skincore.reflex

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.view.ContextThemeWrapper
import java.lang.reflect.Method

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
val addAssetPathMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
    method.isAccessible = true
    method
}
val setApkAssetMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod(
        "setApkAssets",
        Array<String>::class.java, Boolean::class.java
    )
    method.isAccessible = true
    method
}

fun Context.switchBaseContext(base: Context) {
    contextBaseFiled.set(this, base)
}
