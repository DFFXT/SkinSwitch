package com.skin.skincore

import android.app.Application
import android.content.Context
import android.view.View
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.AssetLoader
import com.skin.skincore.collector.isNight
import com.skin.skincore.loader.ContextLoader
import com.skin.skincore.loader.ContextLoaderServer
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.plug.SkinPackDeveloping
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ResourceProviderFactory
import com.skin.skincore.provider.ResourcesProviderManager

/**
 * 皮肤管理器
 */
object SkinManager {
    const val DEFAULT_THEME = 0
    private val loaderServer = ContextLoaderServer()
    private lateinit var providerFactory: ResourceProviderFactory
    private lateinit var application: Application
    private var theme: Int = DEFAULT_THEME
    private var isNight: Boolean = false

    /**
     * 对当前context进行初始化，凡是通过该context进行inflate的对象均进行view拦截
     */
    fun init(ctx: Application, providerFactory: ResourceProviderFactory) {
        this.application = ctx
        this.providerFactory = providerFactory
        isNight = ctx.resources.isNight()
        ResourcesProviderManager.init(ctx, providerFactory)
        SkinPackDeveloping.sinkPackInstall(ctx)
        makeContextSkinAble(ctx)
        ActivitiesCallback.register(ctx)
    }

    /**
     * 使对应context支持换肤
     */
    fun makeContextSkinAble(context: Context) {
        if (!loaderServer.containsContext(context)) {
            val asset = AssetLoader.getAsset(
                application,
                ResourcesProviderManager.getPathProvider(theme)?.getSkinPath()
            )
            loaderServer.addLoader(
                ContextLoader(
                    context,
                    asset,
                    ResourcesProviderManager.getResourceProvider(context, theme),
                    AttrApplyManager.parser
                )
            )
        }
        applyThemeNight(isNight, false, context)
    }

    fun destroy(ctx: Context) {
        loaderServer.removeLoader(ctx)
    }

    /**
     * 新增支持的属性
     */
    fun <T: View> addAttributeCollection(apply: BaseViewApply<T>) {
        AttrApplyManager.addViewApply(apply)
    }

    /**
     * 皮肤切换，将对应的context进行切换
     * @param ctx 如果为null，单独切换，如果不null全局切换
     * 如果页面比较多建议分批次调用
     */
    fun switchTheme(theme: Int, ctx: Context? = null) {
        this.theme = theme
        val asset = AssetLoader.getAsset(
            application,
            ResourcesProviderManager.getPathProvider(theme)?.getSkinPath()
        )
        loaderServer.switchTheme(
            asset,
            ResourcesProviderManager.getResourceProvider(application, theme),
            ctx
        )
    }
    fun applyThemeNight(isNight: Boolean, refresh: Boolean, context: Context? = null) {
        this.isNight = isNight
        // 更新当前MergeResource中的Resource
        loaderServer.applyNight(isNight, context)
        // 更新AssetLoader中已经加载的Resource
        ResourcesProviderManager.applyNight(isNight)
        if (refresh) {
            forceRefreshView()
        }
    }

    fun getCurrentTheme() = theme

    /**
     * 强制刷新，比如白天黑夜变化时可以调用
     */
    fun forceRefreshView(context: Context? = null) {
        loaderServer.forceRefreshView()
    }

    /**
     * 获取当前context的资源提供器，如果context不是可换肤context，则返回默认资源提供器
     */
    fun getResourceProvider(context: Context): IResourceProvider {
        return loaderServer.getContextLoader(context)?.getResourceProvider()
            ?: ResourcesProviderManager.getResourceProvider(context, DEFAULT_THEME)
    }

    /**
     * 从换肤中移除
     */
    fun removeView(view: View) {
        loaderServer.getContextLoader(view.context)?.removeView(view)
    }

    /**
     * 设置app:skin对应值的策略
     * @param skinAttrValue app:skin 设置状态
     * [ParseOutValue.SKIN_ATTR_FALSE]
     * [ParseOutValue.SKIN_ATTR_TRUE]
     * [ParseOutValue.SKIN_ATTR_UNDEFINE]
     * @param apply true 进行换肤、false不进行换肤
     */
    fun setSkinAttrStrategy(skinAttrValue: Int, apply: Boolean) {
        AttrApplyManager.setSkinAttrStrategy(skinAttrValue, apply)
    }
}
