package com.skin.skincore.loader

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.skin.skincore.asset.Asset
import com.skin.skincore.asset.AssetLoader
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.provider.ISkinPathProvider
import com.skin.skincore.provider.MergeResource
import java.lang.ref.WeakReference
import java.lang.reflect.Field

/**
 * Context处理器
 * 将context的resource进行替换
 */
class ContextLoader(context: Context, skinPathProvider: ISkinPathProvider?) {
    val ctxRef = WeakReference(context)

    init {
        InflaterInterceptor.addInterceptor(context)
        switchTheme(skinPathProvider)
    }

    fun switchTheme(skinPathProvider: ISkinPathProvider?) {
        val ctx = ctxRef.get()
        if (ctx != null) {
            if (skinPathProvider == null) {
                val res = ctx.resources
                if (res is MergeResource) {
                    res.switchToDefault()
                }
            } else {
                val asset = AssetLoader.createResource(ctx, skinPathProvider.getSkinPath())!!
                if (ctx is Application) {
                    ContextWrapper::class.java.getDeclaredField("mBase").apply {
                        this.isAccessible = true
                        val cls = Class.forName("android.app.ContextImpl")
                        val filed = cls.getDeclaredField("mResources")
                        updateResourceFiledOrSet(ctx.baseContext, filed, asset)
                    }
                } else if (ctx is AppCompatActivity) {
                    val filed = AppCompatActivity::class.java.getDeclaredField("mResources")
                    updateResourceFiledOrSet(ctx, filed, asset)
                }
            }
        }
    }

    private fun updateResourceFiledOrSet(ctx: Context, field: Field, asset: Asset) {
        field.isAccessible = true
        if (ctx.resources is MergeResource) {
            (ctx.resources as MergeResource).setSkinTheme(asset.res, asset.pkgName)
        } else {
            field.set(
                ctx,
                MergeResource(asset.res, asset.pkgName, ctx.resources)
            )
        }
    }

    /**
     * 销毁该loader，进行资源释放
     */
    fun destroy() {
    }
}
