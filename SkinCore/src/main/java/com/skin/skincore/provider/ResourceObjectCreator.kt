package com.skin.skincore.provider

import android.content.Context
import android.content.res.Resources
import com.skin.skincore.asset.IAsset

/**
 * 创建Resource对象，用于替换原生对象
 */
interface ResourceObjectCreator {
    /**
     * @param asset 资源地址
     * @param default 默认Resource对象，被替换对象
     * @param themeId 当前Resource主题
     */
    fun createResourceObject(
        asset: IAsset,
        defaultContext: Context,
        themeIds: IntArray
    ): MergeResource
}
