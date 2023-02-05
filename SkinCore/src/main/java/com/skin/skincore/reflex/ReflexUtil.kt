package com.skin.skincore.reflex

import android.content.ContextWrapper
import android.content.res.AssetManager
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Field
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

val factoryFiled = LayoutInflater::class.java.getDeclaredField("mFactory").apply {
    isAccessible = true
}
val factory2Filed = LayoutInflater::class.java.getDeclaredField("mFactory2").apply {
    isAccessible = true
}
val privateFactoryFiled =
    LayoutInflater::class.java.getDeclaredField("mPrivateFactory")
        .apply { isAccessible = true }

val constructorArgsFiled =
    LayoutInflater::class.java.getDeclaredField("mConstructorArgs")
        .apply { isAccessible = true }

val addAssetPathMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
    method.isAccessible = true
    method
}

// 普通context（ContextImpl）的resource字段
val contextResourcesField: Field by lazy {
    val cls = Class.forName("android.app.ContextImpl")
    val filed = cls.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}

// AppCompatActivity的resource字段
val avtivityResourcesFiled by lazy {
    val filed = AppCompatActivity::class.java.getDeclaredField("mResources")
    filed.isAccessible = true
    filed
}

val setApkAssetMethod: Method by lazy {
    val method = AssetManager::class.java.getDeclaredMethod(
        "setApkAssets",
        Array<String>::class.java,
        Boolean::class.java
    )
    method.isAccessible = true
    method
}
