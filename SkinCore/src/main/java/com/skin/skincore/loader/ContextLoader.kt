package com.skin.skincore.loader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.IAsset
import com.skin.skincore.collector.ViewContainer
import com.skin.skincore.collector.applyNight
import com.skin.skincore.collector.getViewUnion
import com.skin.skincore.inflater.IOnViewCreated
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.parser.AttrParseInterceptor
import com.skin.skincore.parser.AttrParseListener
import com.skin.skincore.parser.IParser
import com.skin.skincore.plug.updateResource
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.MergeResource
import com.skin.skincore.tag.TAG_CREATE_VIEW
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * Context处理器
 * 将context的resource进行替换，记录inflater生成的view
 * @param asset 当为null时为默认资源
 */
internal class ContextLoader(
    context: Context,
    private var asset: IAsset?,
    private var iResourceProvider: IResourceProvider,
    private val parser: IParser,
) {
    companion object {
        // 当view创建后立即进行换肤操作
        var applyWhenCreate = true
    }

    // 视图解析拦截
    internal var interceptor: AttrParseInterceptor? = null
    internal lateinit var attrParseListeners: LinkedList<AttrParseListener>
    private val viewContainer = ViewContainer()
    private val ctxRef = WeakReference(context)

    init {
        val event = intArrayOf(BaseViewApply.EVENT_TYPE_CREATE)
        InflaterInterceptor.addInterceptor(
            context,
            object : IOnViewCreated {
                override fun onViewCreated(
                    parent: View?,
                    view: View,
                    name: String,
                    attributeSet: AttributeSet,
                ) {
                    // 判断是否拦截
                    if (interceptor?.beforeParse(parent, view, attributeSet) != true) {
                        val union = parser.parse(parent, view, attributeSet)
                        viewContainer.add(view, union)
                        attrParseListeners.forEach {
                            it.onAttrParsed(parent, view, attributeSet, union)
                        }
                        Logger.v(TAG_CREATE_VIEW, "listen view created ok:$view")
                        // view生成，如果是其它皮肤，则立即应用，因为background等属性是通过TypedArray来获取的
                        if (applyWhenCreate) {
                            AttrApplyManager.apply(event, view, union, iResourceProvider)
                        }
                    }
                }
            },
        )
        switchTheme(asset, iResourceProvider, event)
    }

    fun switchTheme(asset: IAsset?, iResourceProvider: IResourceProvider, eventType: IntArray) {
        this.iResourceProvider = iResourceProvider
        // this.asset = asset
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
            // ctx.updateResource(asset)
        }
        refreshView(eventType)
    }

    fun applyNight(isNight: Boolean) {
        val ctx = ctxRef.get()
        ctx?.resources?.applyNight(isNight)
    }

    fun getContextReference(): WeakReference<Context> = ctxRef

    /**
     * 强制刷新View
     */
    fun refreshView(eventType: IntArray) {
        viewContainer.forEach { viewRef ->
            val v = viewRef.key
            val union = v.getViewUnion()
            if (union != null) {
                AttrApplyManager.apply(eventType, v, union, iResourceProvider)
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
