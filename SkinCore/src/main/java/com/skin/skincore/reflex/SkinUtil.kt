package com.skin.skincore.reflex

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.skin.log.Logger
import com.skin.skincore.provider.MergeResource
import java.lang.reflect.Field
import java.lang.reflect.Method

val contextBaseFiled by lazy {
    ContextWrapper::class.java.getDeclaredField("mBase").apply {
        isAccessible = true
    }
}

internal val inflater by lazy {
    ContextThemeWrapper::class.java.getDeclaredField("mInflater").apply {
        isAccessible = true
    }
}

internal val factoryFiled = LayoutInflater::class.java.getDeclaredField("mFactory").apply {
    isAccessible = true
}
internal val factory2Filed = LayoutInflater::class.java.getDeclaredField("mFactory2").apply {
    isAccessible = true
}
internal val privateFactoryFiled =
    LayoutInflater::class.java.getDeclaredField("mPrivateFactory")
        .apply { isAccessible = true }


internal val constructorArgsFiled by lazy { Class.forName("android.view.LayoutInflater").getDeclaredField("mConstructorArgs")
    .apply { isAccessible = true } }


internal val addAssetPathMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
    method.isAccessible = true
    method
}

// 普通context（ContextImpl）的resource字段
internal val contextResourcesField: Field by lazy {
    val cls = Class.forName("android.app.ContextImpl")
    val filed = cls.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}

// AppCompatActivity的resource字段
internal val avtivityResourcesFiled by lazy {
    val filed = AppCompatActivity::class.java.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}


internal val themeWrapperResourcesFiled by lazy {
    val filed = ContextThemeWrapper::class.java.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}
internal val customThemeWrapperResourcesFiled by lazy {
    val filed = androidx.appcompat.view.ContextThemeWrapper::class.java.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}

internal val setApkAssetMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod(
        "setApkAssets",
        Array<String>::class.java,
        Boolean::class.java
    )
    method.isAccessible = true
    method
}

/**
 * 获取当前context设置的themeId
 */
@SuppressLint("SoonBlockedPrivateApi")
internal fun Context.getCurrentThemeId(): Int {
    return try {
        val appliedStyleId = Resources.Theme::class.java.getDeclaredMethod("getAppliedStyleResId").let {
            it.isAccessible = true
            it.invoke(theme) as Int
        }
        appliedStyleId
    } catch (e: Exception) {
        Logger.e("getCurrentThemeId", "getCurrentThemeId error:${e.message}")
        0
    }
}

/**
 * 获取当前Context的Theme，如果是切换了皮肤，就返回皮肤中的Theme
 */
fun Context.getSkinTheme(): Theme? {
    val res = resources
    if (res is MergeResource && !res.useDefault) {
        return res.theme
    }
    return theme
}
