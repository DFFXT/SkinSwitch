package com.skin.skincore.plug

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.skin.skincore.asset.Asset
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.reflex.avtivityResourcesFiled
import com.skin.skincore.reflex.contextResourcesField
import com.skin.skincore.reflex.getCurrentThemeId

/**
 * 将Context的resource替换为MergeResource，如果已经是了，则切换为皮肤包资源
 */
internal fun Context.updateResource(asset: Asset) {
    // application 和其他context不一样
    val target = if (this is Application) {
        this.baseContext
    } else {
        this
    }
    // activity和context的resource字段不一样
    val filed = if (this is AppCompatActivity) {
        avtivityResourcesFiled
    } else {
        contextResourcesField
    }
    if (this.resources is MergeResource) {
        // 资源切换
        (this.resources as MergeResource).setSkinTheme(asset)
    } else {
        // 资源设置
        filed.set(
            target,
            MergeResource(asset, this.resources, getCurrentThemeId())
        )
    }
}
