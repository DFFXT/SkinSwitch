package com.skin.skincore.asset

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources

class AssetLoader {

    fun createResource(context: Context, path: String): Resources {
        val manager1 = AssetManager::class.java.newInstance()
        val addFiled =
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
        addFiled.isAccessible = true
        addFiled.invoke(manager1, path)
        return Resources(
            manager1,
            context.resources.displayMetrics,
            context.resources.configuration
        )
    }
}
