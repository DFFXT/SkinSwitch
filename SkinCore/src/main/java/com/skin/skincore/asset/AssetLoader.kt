package com.skin.skincore.asset

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.skin.skincore.reflex.addAssetPathMethod

object AssetLoader {

    fun createResource(context: Context, path: String): Asset? {

        val pm = context.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SERVICES)
        val pkgName = pkgInfo?.packageName
        if (pkgName.isNullOrEmpty()) return null
        try {
            val manager1 = AssetManager::class.java.newInstance()
            addAssetPathMethod.invoke(manager1, path)

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
