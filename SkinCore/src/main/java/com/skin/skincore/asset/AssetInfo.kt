package com.skin.skincore.asset

import android.content.res.AssetManager

/**
 * 皮肤包生成的资源信息
 * @param assetManager 皮肤包资源管理器
 * @param pkgName 皮肤包包名
 */
data class AssetInfo(
    val assetManager: AssetManager,
    val pkgName: String
)