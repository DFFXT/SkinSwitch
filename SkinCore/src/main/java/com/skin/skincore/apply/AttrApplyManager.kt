package com.skin.skincore.apply

import android.content.res.Resources
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.core.util.keyIterator
import com.skin.log.Logger
import com.skin.skincore.OnThemeChangeListener
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ResType
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.DefaultParser
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.getSkinTheme

/**
 * 换肤执分发器
 */
object AttrApplyManager {
    private val skinAttrStrategy = SparseBooleanArray()
    private val applySet: SparseArray<BaseViewApply<View>> = SparseArray()
    // 可蓝拦截换肤view
    var onApplyInterceptor: AttrApplyInterceptor? = null

    // 属性解析器
    internal val parser by lazy { DefaultParser(applySet.keyIterator().asSequence().toHashSet()) }

    init {
        addViewApplyInternal(AttrBackgroundApply())
        addViewApplyInternal(AttrBackgroundTintApply())
        addViewApplyInternal(AttrSrcApply())
        addViewApplyInternal(AttrTextColorApply())
        addViewApplyInternal(AttrDrawableBottomApply())
        addViewApplyInternal(AttrDrawableTopApply())
        addViewApplyInternal(AttrDrawableStartApply())
        addViewApplyInternal(AttrDrawableEndApply())
        addViewApplyInternal(AttrButtonApply())
        addViewApplyInternal(AttrForegroundTintApply())
        addViewApplyInternal(AttrForegroundApply())
        addViewApplyInternal(AttrProgressDrawableApply())
        addViewApplyInternal(AttrTextColorHintApply())
        addViewApplyInternal(AttrThumbApply())
        addViewApplyInternal(AttrIndeterminateDrawableApply())

        // app:skin="false" 则不换肤
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_FALSE, false)
        // app:skin="true" 或者没有设置 则换肤
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_TRUE, true)
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_UNDEFINE, true)
    }

    internal fun triggerApply(eventType: IntArray, view: View, union: ViewUnion, provider: IResourceProvider) {
        // 如果有设置，则优先判断当前属性
        if (union.skinAttrValue != ParseOutValue.SKIN_ATTR_UNDEFINE) {
            if (!skinAttrStrategy.get(union.skinAttrValue)) return
        } else {
            // 如果没有设置，则使用继承属性，判断是否需要执行
            if (!skinAttrStrategy.get(union.skinInheritedValue)) return
        }
        if (onApplyInterceptor?.onApply(view, eventType) == true) {
            return
        }
        apply(eventType, view, union, provider)
    }

    /**
     * 触发view换肤
     */
    fun apply(eventType: IntArray, view: View, union: ViewUnion, provider: IResourceProvider) {
        // 在应用属性变化时触发监听
        if (view is OnThemeChangeListener) {
            view.onThemeChanged(SkinManager.getCurrentTheme(), SkinManager.isNightMode(), eventType)
        }
        union.forEach {
            try {
                apply(it.attributeId, eventType, view, it.resId, it.getResourceType(view.resources), provider, view.context.getSkinTheme())
            } catch (e: ClassCastException) {
                Logger.e("AttrApplyManager", "not limit attribute range error")
                e.printStackTrace()
            }
        }
    }

    /**
     * 触发属性更新
     */
    fun apply(
        attributeId: Int,
        eventType: IntArray,
        view: View,
        resId: Int,
        @ResType resType: String,
        provider: IResourceProvider,
        theme: Resources.Theme?,
    ) {
        applySet[attributeId]?.tryApply(
            eventType,
            view,
            resId,
            resType,
            provider,
            theme,
        )
    }

    /**
     * 新增其他处理器
     */
    internal fun <T : View> addViewApply(apply: BaseViewApply<T>) {
        applySet.put(apply.supportAttribute, apply as BaseViewApply<View>)
        parser.addSupportAttr(apply.supportAttribute)
    }

    /**
     * 设置app:skin对应值的策略
     */
    internal fun setSkinAttrStrategy(skinAttrValue: Int, apply: Boolean) {
        skinAttrStrategy.put(skinAttrValue, apply)
    }

    private fun <T : View> addViewApplyInternal(apply: BaseViewApply<T>) {
        applySet.put(apply.supportAttribute, apply as BaseViewApply<View>)
    }

    interface AttrApplyInterceptor {
        /**
         * 换肤拦截
         * false 不拦截；true拦截
         * @param eventType 事件类型[BaseViewApply.EVENT_TYPE_THEME]等
         */
        fun onApply(view: View, eventType: IntArray): Boolean
    }
}
