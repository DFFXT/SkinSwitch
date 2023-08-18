package com.skin.skincore.plug

import android.app.Application
import android.content.Context
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.skin.skincore.asset.IAsset
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.provider.ResourcesProviderManager
import com.skin.skincore.reflex.avtivityResourcesFiled
import com.skin.skincore.reflex.contextResourcesField
import com.skin.skincore.reflex.contextThemeFiled
import com.skin.skincore.reflex.contextThemeId
import com.skin.skincore.reflex.customContextThemeFiled
import com.skin.skincore.reflex.customContextThemeId
import com.skin.skincore.reflex.getResourcesKey
import com.skin.skincore.reflex.resourceClassLoader
import com.skin.skincore.reflex.resourcesImplFiled
import com.skin.skincore.reflex.themeWrapperResourcesFiled

/**
 * 将Context的resource替换为MergeResource，如果已经是了，则切换为皮肤包资源
 */
internal fun Context.updateResource(asset: IAsset) {
    // application 和其他context不一样
    val target = if (this is Application) {
        this.baseContext
    } else {
        this
    }
    // activity和context的resource字段不一样
    val filed = if (this is AppCompatActivity) {
        avtivityResourcesFiled
    } else if (this is ContextThemeWrapper) {
        // 存在ContextThemeWrapper不是activity实例的情况
        themeWrapperResourcesFiled
    } else {
        contextResourcesField
    }
    if (this.resources is MergeResource) {
        // 资源切换
        (this.resources as MergeResource).setSkinTheme(asset)
    } else {

        // 获取当前theme设置过的主题
        val keys = this.theme.getResourcesKey()
        // 资源设置
        val mergeResource = ResourcesProviderManager.resourceObjectCreator.createResourceObject(
            asset,
            this,
            keys,
        )

        /**
         * 曾经遇到过的问题：
         * 1、dialog的theme异常，导致textLinkColor无法解析，必须手动指定dialog主题
         * 解决方法：删除context的里面的theme对象，重新设置themeId
         * 2、解决问题1后，发现，DrawableInflater中classLoader是默认资源的classLoader，导致drawable中无法通过类名实例化Drawable类型对象
         * 解决方法：反射修改MergeResource中的classLoader
         */
        // 设置impl
        val impl = resourcesImplFiled.get(this.resources)
        resourcesImplFiled.set(mergeResource, impl)
        val keys1 = this.theme.getResourcesKey()

        // 将设置的主题资源id重置
        if (this is ContextThemeWrapper) {
            contextThemeFiled?.set(this, null)
            contextThemeId.set(this, 0)
        }
        if (this is androidx.appcompat.view.ContextThemeWrapper) {
            customContextThemeFiled.set(this, null)
            customContextThemeId.set(this, 0)
        }
        // 修改Resources里面的classLoader，否则无法使用DrawableInflater功能(drawable类型xml中根据className创建drawable对象)
        resourceClassLoader.set(mergeResource, resourceClassLoader.get(this.resources))
        filed.set(target, mergeResource)
        // 重新设置theme
        keys1.forEach {
            this.setTheme(it)
        }

    }
}
