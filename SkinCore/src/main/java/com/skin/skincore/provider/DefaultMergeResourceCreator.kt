package com.skin.skincore.provider

import android.content.Context
import com.skin.skincore.asset.IAsset

/**
 * 默认Resource对象创建
 */
object DefaultMergeResourceCreator : ResourceObjectCreator {
    override fun createResourceObject(
        asset: IAsset,
        defaultContext: Context,
        themeIds: IntArray
    ): MergeResource {
        return MergeResource(asset, defaultContext.resources, themeIds)
    }
}
