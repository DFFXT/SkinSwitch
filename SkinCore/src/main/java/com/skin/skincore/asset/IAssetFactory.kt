package com.skin.skincore.asset

import android.content.Context
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源生成器
 */
interface IAssetFactory {
    fun createAsset(context: Context, skinPathProvider: ISkinPathProvider): IAsset
}
