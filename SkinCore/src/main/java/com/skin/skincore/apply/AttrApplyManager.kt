package com.skin.skincore.apply

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.core.util.keyIterator
import com.skin.log.Logger
import com.skin.skincore.OnThemeChangeListener
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.DefaultParser
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.getSkinTheme

/**
 * 换肤执分发器
 */
internal object AttrApplyManager {
    private val skinAttrStrategy = SparseBooleanArray()
    private val applySet: SparseArray<BaseViewApply<View>> = SparseArray()

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

    fun apply(eventType: IntArray, view: View, union: ViewUnion, provider: IResourceProvider) {
        // 如果有设置，则优先判断当前属性
        if (union.skinAttrValue != ParseOutValue.SKIN_ATTR_UNDEFINE) {
            if (!skinAttrStrategy.get(union.skinAttrValue)) return
        } else {
            // 如果没有设置，则使用继承属性，判断是否需要执行
            if (!skinAttrStrategy.get(union.skinInheritedValue)) return
        }
        // 在应用属性变化时触发监听
        if (view is OnThemeChangeListener) {
            view.onThemeChanged(SkinManager.getCurrentTheme(), SkinManager.isNightMode(), eventType)
        }
        union.forEach {
            try {
                applySet[it.attributeId]?.tryApply(
                    eventType,
                    view,
                    it.resId,
                    it.getResourceType(view.resources),
                    provider,
                    view.context.getSkinTheme()
                )
            } catch (e: ClassCastException) {
                Logger.e("AttrApplyManager", "not limit attribute range error")
                e.printStackTrace()
            }
        }
    }

    /**
     * 新增其他处理器
     */
    fun <T : View> addViewApply(apply: BaseViewApply<T>) {
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
}
