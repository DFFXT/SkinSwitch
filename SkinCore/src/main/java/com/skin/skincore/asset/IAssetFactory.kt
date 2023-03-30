package com.skin.skincore.asset

import android.app.Application
import com.skin.skincore.provider.ISkinPathProvider

/**
 * 资源生成器
 */
interface IAssetFactory {
    fun createAsset(application: Application, skinPathProvider: ISkinPathProvider): IAsset
}
