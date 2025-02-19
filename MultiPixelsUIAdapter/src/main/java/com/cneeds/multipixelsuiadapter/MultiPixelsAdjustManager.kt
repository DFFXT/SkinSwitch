package com.cneeds.multipixelsuiadapter

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import com.skin.skincore.SkinManager
import com.skin.skincore.collector.ViewUnion
import com.skin.skincore.parser.AttrParseListener
import java.util.LinkedList

/**
 * 多分辨率适配方案
 * 在解析布局xml时会回调，通过计算当前最适合的宽高比，选择合适的资源后缀来进行分辨率适配
 */
object MultiPixelsAdjustManager {

    private val TAG = "MultiPixelsAdjustManager"
    internal lateinit var application: Application
    private val mMultiPixelsApplyMap = HashMap<Int, BaseMultiPixelsApply<*>>()
    private lateinit var sortedAttributeIds: IntArray
    private val multiPixelsAdjust: MultiPixelsAdjust = MultiPixelsAdjust()

    /**
     * 初始化
     * @param autoInitSkinCore 是否自动初始化SkinCore组件
     */
    fun init(context: Application) {
        application = context
        // 添加一些默认的分辨率适配属性，
        addMultiPixelsApply(MultiPixelsLayoutWidthApply())
        addMultiPixelsApply(MultiPixelsLayoutHeightApply())
        addMultiPixelsApply(MultiPixelsTextSizeApply())

        // 初始化
        makeContextMultiPixelsAble(context)
        context.registerActivityLifecycleCallbacks(ActivityCallbacks())
        context.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                // 如果分辨率变化，需要重新计算
                multiPixelsAdjust.updateConfiguration(context)
            }

            override fun onLowMemory() {
            }
        })


        SkinManager.addViewAttrParseListener(object : AttrParseListener {
            private val runnableAfterInflateList =
                LinkedList<BaseMultiPixelsApply.RunnableAfterInflate>()

            override fun onInflateFinish(rootView: View) {
                for (runnable in runnableAfterInflateList) {
                    runnable.run()
                }
                runnableAfterInflateList.clear()
            }

            override fun onAttrParsed(
                parent: View?,
                view: View,
                attributeSet: AttributeSet,
                union: ViewUnion
            ) {
                val attrs = view.context.obtainStyledAttributes(attributeSet, sortedAttributeIds)
                sortedAttributeIds.forEachIndexed { index, attr ->
                    if (attrs.hasValue(index)) {
                        val apply = mMultiPixelsApplyMap[attr]
                        val resId = attrs.getResourceId(index, 0)
                        if (resId != 0) {
                            val runnable = apply?.tryApply(view, resId)
                            // 加入列表等待执行
                            if (runnable != null) {
                                runnableAfterInflateList.add(runnable)
                            }
                        }
                    }
                }

                attrs.recycle()
            }

        })
    }

    /**
     * 使当前context具有多分辨率适配功能
     */
    fun makeContextMultiPixelsAble(context: Context) {

    }

    /**
     * 添加属性适配执行器
     */
    fun addMultiPixelsApply(apply: BaseMultiPixelsApply<*>) {
        mMultiPixelsApplyMap[apply.supportAttribute] = apply
        sortedAttributeIds = mMultiPixelsApplyMap.keys.sorted().toIntArray()
    }

    /**
     * 添加分辨率比例以及后缀表
     * 当添加完成后需要调用recalculate触发计算
     */
    fun addMultiPixelsSuffix(uiConfig: MultiPixelsAdjust.UIConfig, suffix: String, recalculate: Boolean = false) {
        multiPixelsAdjust.addMultiPixelsSuffix(uiConfig, suffix, recalculate)
    }

    /**
     * 重新计算
     */
    fun recalculate(context: Context = application) {
        multiPixelsAdjust.updateConfiguration(context)
    }

    fun getCurrentResourcesSuffix() = multiPixelsAdjust.currentUseSuffix

}

/**
 * 根据后缀获取资源id
 */
fun Int.getResourceByScale(context: Context? = null): Int {
    // 如果当前没有设置后缀，则直接返回原资源id
    val resId = this
    val suffix = MultiPixelsAdjustManager.getCurrentResourcesSuffix()
    if (suffix.isEmpty()) return resId
    try {
        val ctx = context ?: MultiPixelsAdjustManager.application
        val name = ctx.resources.getResourceEntryName(resId)
        val type = ctx.resources.getResourceTypeName(resId)
        val pkgName = ctx.resources.getResourcePackageName(resId)
        val id = ctx.resources.getIdentifier(name + suffix, type, pkgName)
        return if (id == 0) resId else id
    } catch (_:Throwable){
    }
    return resId
}