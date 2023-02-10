package com.skin.skincore.apply

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import androidx.core.util.keyIterator
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.ParseOutValue
import com.skin.skincore.provider.IResourceProvider
import com.skin.skincore.reflex.getSkinTheme

/**
 * 换肤执分发器
 */
object AttrApplyManager {
    private val skinAttrStrategy = SparseBooleanArray()
    private val applySet: SparseArray<BaseViewApply<View>> = SparseArray()

    init {
        addSkinAttrApply(AttrBackgroundApply())
        addSkinAttrApply(AttrSrcApply())
        addSkinAttrApply(AttrTextColorApply())
        addSkinAttrApply(AttrDrawableBottomApply())
        addSkinAttrApply(AttrDrawableTopApply())
        addSkinAttrApply(AttrDrawableStartApply())
        addSkinAttrApply(AttrDrawableEndApply())
        // app:skin="false" 则不换肤
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_FALSE, false)
        // app:skin="true" 或者没有设置 则换肤
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_TRUE, true)
        SkinManager.setSkinAttrStrategy(ParseOutValue.SKIN_ATTR_UNDEFINE, true)
    }

    fun apply(view: View, union: ViewUnion, provider: IResourceProvider) {
        // 当前策略不支持换肤
        if (!skinAttrStrategy.get(union.getSkinAtrValue())) return

        union.forEach {
            // todo 使用hash，降低时间复杂度
            applySet[it.attributeId]?.apply(view, it.resId, it.resourceType, provider, view.context.getSkinTheme())
        }
    }

    /**
     * 新增其他处理器
     */
    fun <T : View> addViewApply(apply: BaseViewApply<T>) {
        applySet.put(apply.supportAttribute, apply as BaseViewApply<View>)
    }

    /**
     * 设置app:skin对应值的策略
     */
    fun setSkinAttrStrategy(skinAttrValue: Int, apply: Boolean) {
        skinAttrStrategy.put(skinAttrValue, apply)
    }

    fun getSupportAttributeId(): List<Int> = applySet.keyIterator().asSequence().toList()

    private fun <T : View> addSkinAttrApply(apply: BaseViewApply<T>) {
        applySet.put(apply.supportAttribute, apply as BaseViewApply<View>)
    }
}
