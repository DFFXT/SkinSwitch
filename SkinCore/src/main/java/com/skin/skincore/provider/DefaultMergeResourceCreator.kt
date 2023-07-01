package com.skin.skincore.provider

import android.content.res.Resources
import com.skin.skincore.asset.IAsset

/**
 * 默认Resource对象创建
 */
object DefaultMergeResourceCreator : ResourceObjectCreator {
    override fun createResourceObject(
        asset: IAsset,
        default: Resources,
        themeId: Int,
    ): MergeResource {
        return MergeResource(asset, default, themeId)
    }
}
