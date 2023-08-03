package com.skin.skincore

import android.app.Application
import android.content.Context
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.asset.AssetLoaderManager
import com.skin.skincore.collector.isNight
import com.skin.skincore.loader.ContextLoader
import com.skin.skincore.loader.ContextLoaderServer
import com.skin.skincore.parser.AttrParseInterceptor
import com.skin.skincore.parser.AttrParseListener
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.plug.SkinPackDeveloping
import com.skin.skincore.plug.SpeedUpSwitchSkin
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.provider.ResourceProviderFactory
import com.skin.skincore.provider.ResourcesProviderManager

/**
 * 皮肤管理器
 */
object SkinManager {
    private const val TAG = "SkinManager_"
    const val DEFAULT_THEME = 0
    private val skinChangeListenerSet = HashSet<OnThemeChangeListener>()
    private val loaderServer = ContextLoaderServer()
    private lateinit var providerFactory: ResourceProviderFactory
    private lateinit var application: Application
    private var theme: Int = DEFAULT_THEME
    private var isNight: Boolean = false
    var projectStyle: Int = 0

    /**
     * 对当前context进行初始化，凡是通过该context进行inflate的对象均进行view拦截
     * @param projectStyle 当前应用主题样式，值最好为androidManifest.xml中theme属性，可为0
     * @param providerFactory 资源提供器，一般继承[DefaultProviderFactory]
     * @param delayDetachedView 是否延迟切换detached状态的View，比如RecyclerView缓存、remove了但仍待使用的view；默认不开启
     */
    fun init(ctx: Application, projectStyle: Int, providerFactory: ResourceProviderFactory, isNight: Boolean = ctx.resources.isNight(), delayDetachedView: Boolean = false) {
        this.application = ctx
        this.projectStyle = projectStyle
        this.providerFactory = providerFactory
        this.isNight = isNight
        ResourcesProviderManager.init(ctx, providerFactory)
        SkinPackDeveloping.sinkPackInstall(ctx)
        makeContextSkinAble(ctx)
        ActivitiesCallback.register(ctx)
        if (delayDetachedView) {
            SpeedUpSwitchSkin().init()
        }
    }

    /**
     * 是否在view创建时就应用换肤
     * @param apply true 会额外耗费时间进行换肤；false 页面启动快30%左右，前提是没有使用theme相关的样式
     */
    fun applyWhenCreate(apply: Boolean) {
        ContextLoader.applyWhenCreate = apply
    }

    /**
     * 使对应context支持换肤
     */
    fun makeContextSkinAble(context: Context) {
        if (!loaderServer.containsContext(context)) {
            val asset = AssetLoaderManager.getAsset(
                context,
                ResourcesProviderManager.getPathProvider(theme),
            )
            loaderServer.addLoader(
                ContextLoader(
                    context,
                    asset,
                    ResourcesProviderManager.getResourceProvider(context, theme),
                    AttrApplyManager.parser,
                ),
            )
        }
        applyThemeNight(isNight, context)
    }

    fun destroy(ctx: Context) {
        loaderServer.removeLoader(ctx)
    }

    /**
     * 新增支持的属性
     * 不仅支持换肤
     * 还可以添加其他属性，比如layout_width 从而达到动态横竖屏切换
     * 还可以添加 android:text 从而达到动态切换语言
     */
    fun <T : View> addAttributeCollection(apply: BaseViewApply<T>) {
        AttrApplyManager.addViewApply(apply)
    }

    /**
     * 皮肤切换，将对应的context进行切换
     * @param ctx 如果为null，单独切换，如果不null全局切换
     * 如果页面比较多建议分批次调用
     * @param isNight 使用当前皮肤包的哪种模式
     * @param eventType 事件类型，默认[BaseViewApply.EVENT_TYPE_THEME]换肤事件，可自定义，对应的
     * 需要[BaseViewApply]里面的eventType与之对应
     */
    fun switchTheme(theme: Int, ctx: Context? = null, isNight: Boolean? = this.isNight, eventType: IntArray = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)) {
        Logger.d(TAG, "switchTheme $theme $ctx $isNight ${eventType.joinToString(",")}")
        val asset = AssetLoaderManager.getAsset(
            application,
            ResourcesProviderManager.getPathProvider(theme),
        )
        if (this.theme != theme || this.isNight != isNight) {
            this.isNight = isNight ?: this.isNight
            // 应用资源的白天黑夜模式
            applyNightMode(this.isNight, ctx)
        }
        loaderServer.switchTheme(
            asset,
            ResourcesProviderManager.getResourceProvider(application, theme),
            ctx,
            eventType,
        )

        this.theme = theme
    }

    /**
     * 切换白天黑夜
     */
    fun applyThemeNight(isNight: Boolean, context: Context? = null) {
        Logger.d(TAG, "applyThemeNight start $isNight $context")
        this.isNight = isNight
        applyNightMode(isNight, context)
        forceRefreshView(context)
        dispatchSkinChange(theme, isNight, intArrayOf(BaseViewApply.EVENT_TYPE_THEME))
        Logger.d(TAG, "applyThemeNight end $isNight $context")
    }

    /**
     * 当前白天黑夜模式
     */
    fun isNightMode() = isNight

    /**
     * 当前主题
     */
    fun getCurrentTheme() = theme

    /**
     * 强制刷新，比如白天黑夜变化时可以调用
     * context 为null则全局刷新，不为null则只刷新该context生成的view
     */
    fun forceRefreshView(context: Context? = null, eventType: IntArray = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)) {
        loaderServer.forceRefreshView(context, eventType)
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

    fun addSkinChangeListener(listener: OnThemeChangeListener) {
        skinChangeListenerSet.add(listener)
    }

    fun removeSkinChangeListener(listener: OnThemeChangeListener) {
        skinChangeListenerSet.remove(listener)
    }

    fun applyNightMode(isNight: Boolean, context: Context?) {
        // 更新当前MergeResource中的Resource
        loaderServer.applyNight(isNight, context)
        // 更新AssetLoader中已经加载的Resource
        Logger.d(TAG, "applyNightMode $isNight")
        ResourcesProviderManager.applyNight(isNight)
    }

    private fun dispatchSkinChange(theme: Int, isNight: Boolean, eventType: IntArray) {
        skinChangeListenerSet.forEach {
            it.onThemeChanged(theme, isNight, eventType)
        }
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

    /**
     * 设置视图解析拦截器
     */
    fun setViewAttrParseInterceptor(interceptor: AttrParseInterceptor) {
        loaderServer.setAttrParseInterceptor(interceptor)
    }

    /**
     * 设置属性解析监听
     */
    fun addViewAttrParseListener(listener: AttrParseListener) {
        loaderServer.addAttrParseListener(listener)
    }
}
