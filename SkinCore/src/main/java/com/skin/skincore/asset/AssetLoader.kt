package com.skin.skincore.asset

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import java.lang.Exception

object AssetLoader {

    fun createResource(context: Context, path: String): Asset? {

        val pm = context.getSystemService(PackageManager::class.java)
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        val pkgName = pkgInfo?.packageName
        if (pkgName.isNullOrEmpty()) return null
        try {
            val manager1 = AssetManager::class.java.newInstance()
            val addFiled =
                AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addFiled.isAccessible = true
            addFiled.invoke(manager1, path)

            val res = Resources(
                manager1,
                context.resources.displayMetrics,
                context.resources.configuration
            )
            return Asset(pkgName, res)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
