package com.skin.skincore.loader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.asset.Asset
import com.skin.skincore.collector.IAttrCollector
import com.skin.skincore.collector.ViewContainer
import com.skin.skincore.collector.getViewUnion
import com.skin.skincore.inflater.IOnViewCreated
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.plug.updateResource
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.MergeResource
import java.lang.ref.WeakReference

/**
 * Context处理器
 * 将context的resource进行替换
 */
class ContextLoader(
    context: Context,
    asset: Asset?,
    private var iResourceProvider: IResourceProvider,
    private val collectors: IAttrCollector<*>
) {
    val viewContainer = ViewContainer()
    private val ctxRef = WeakReference(context)

    init {
        InflaterInterceptor.addInterceptor(
            context,
            object : IOnViewCreated {
                override fun onViewCreated(view: View, name: String, attributeSet: AttributeSet) {
                    val attrs = collectors.parser.parse(view, attributeSet)
                    viewContainer.add(view, attrs)
                    // view生成，但是没有必要进行apply
                    /*attrs.forEach { attr ->
                        ApplyManger.getApplyDispatcher().apply(view, attr, iResourceProvider)
                    }*/
                }
            }
        )
        switchTheme(asset, iResourceProvider)
    }

    fun switchTheme(asset: Asset?, iResourceProvider: IResourceProvider) {
        this.iResourceProvider = iResourceProvider
        val ctx = ctxRef.get()
        if (ctx != null) {
            if (asset == null) {
                val res = ctx.resources
                if (res is MergeResource) {
                    res.switchToDefault()
                }
            } else {
                ctx.updateResource(asset)
            }
        }
        refreshView()
    }

    /**
     * 强制刷新View
     */
    fun refreshView() {
        viewContainer.forEach { viewRef ->
            val v = viewRef.key
            v.getViewUnion()?.forEach {
                AttrApplyManager.apply(v, it.value, iResourceProvider)
            }
        }
    }

    /**
     * 是否是同一个context
     */
    fun equalContext(ctx: Context): Boolean {
        return ctxRef.get() == ctx
    }

    /**
     * 是否销毁了
     */
    fun isDestroyed(): Boolean {
        return ctxRef.get() == null
    }

    /**
     * 销毁该loader，进行资源释放
     */
    fun destroy() {
    }
}
