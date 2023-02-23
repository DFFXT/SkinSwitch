package com.skin.skincore.loader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.asset.Asset
import com.skin.skincore.collector.*
import com.skin.skincore.inflater.IOnViewCreated
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.parser.IParser
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.plug.updateResource
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.tag.TAG_CREATE_VIEW
import java.lang.ref.WeakReference

/**
 * Context处理器
 * 将context的resource进行替换
 * @param asset 当为null时为默认资源
 */
internal class ContextLoader(
    context: Context,
    asset: Asset?,
    private var iResourceProvider: IResourceProvider,
    private val parser: IParser
) {
    companion object {
        // 当view创建后立即进行换肤操作
        var applyWhenCreate = true
    }

    val viewContainer = ViewContainer()
    private val ctxRef = WeakReference(context)

    init {
        InflaterInterceptor.addInterceptor(
            context,
            object : IOnViewCreated {
                val outValue = ParseOutValue()
                override fun onViewCreated(parent: View?, view: View, name: String, attributeSet: AttributeSet) {
                    val union = parser.parse(parent, view, attributeSet)
                    viewContainer.add(view, union)
                    Logger.i(TAG_CREATE_VIEW, "listen view created ok:$view")
                    // view生成，如果是其它皮肤，则立即应用，因为background等属性是通过TypedArray来获取的
                    if (asset != null && applyWhenCreate) {
                        AttrApplyManager.apply(view, union, iResourceProvider)
                    }
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

    fun applyNight(isNight: Boolean) {
        val res = ctxRef.get()?.resources ?: return
        res.applyNight(isNight)
    }

    fun getContextReference(): WeakReference<Context> = ctxRef

    /**
     * 强制刷新View
     */
    fun refreshView() {
        viewContainer.forEach { viewRef ->
            val v = viewRef.key
            val union = v.getViewUnion()
            if (union != null) {
                AttrApplyManager.apply(v, union, iResourceProvider)
            }
        }
    }

    fun removeView(view: View) {
        viewContainer.remove(view)
    }

    fun getResourceProvider(): IResourceProvider {
        return iResourceProvider
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
