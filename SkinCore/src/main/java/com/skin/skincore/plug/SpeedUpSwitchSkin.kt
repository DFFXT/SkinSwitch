package com.skin.skincore.plug

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import com.skin.log.Logger
import com.skin.skincore.OnThemeChangeListener
import com.skin.skincore.SkinManager
import com.skin.skincore.apply.AttrApplyManager
import com.skin.skincore.apply.base.BaseViewApply
import com.skin.skincore.collector.getViewUnion
import java.util.WeakHashMap

/**
 * 换肤提速
 * 对于不可见的view，延迟换肤，post一下再换肤
 */
open class SpeedUpSwitchSkin : AttrApplyManager.AttrApplyInterceptor, OnThemeChangeListener {
    companion object {
        // 延迟换肤条件，返回值决定是否延迟换肤
        // 当不再viewTree上，而且不是创建，则拦截，延迟换肤
        // 如果刚创建：不拦截，直接切换
        // 如果没有在window上，而且不是刚创建：拦截，延迟切换
        // 如果当前View不显示：拦截，延迟切换
        // 默认不拦截刚创建的view的换肤（EVENT_TYPE_CREATE），EVENT_TYPE_CREATE后不会收到onThemeChanged回调
        var delayApplyStrategy: ((view: View, eventType: IntArray) -> Boolean)? = { view: View, eventType: IntArray ->
            (!view.isAttachedToWindow || !view.isShown) && !eventType.contains(BaseViewApply.EVENT_TYPE_CREATE)
        }
    }

    protected val handler = Handler(Looper.getMainLooper())

    // 需要延迟换肤的view
    protected val viewRef = WeakHashMap<View, Unit>()

    fun init() {
        SkinManager.addSkinChangeListener(this)
        AttrApplyManager.onApplyInterceptor = this
    }

    /**
     * 换肤，post执行延迟换肤
     * 该回调一定在onApplyInterceptor之后执行
     */
    override fun onThemeChanged(theme: Int, isNight: Boolean, eventType: IntArray) {
        handler.post {
            val time = SystemClock.elapsedRealtime()
            Logger.d("SpeedUpSwitchSkin", "onThemeChange delay start")
            val event = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)
            for (ref in viewRef) {
                val v = ref.key
                val union = v.getViewUnion() ?: continue
                AttrApplyManager.apply(event, v, union, SkinManager.getResourceProvider(v.context))
            }
            Logger.d("SpeedUpSwitchSkin", "onThemeChange delay finish. delay size: ${viewRef.size}, cost time:${SystemClock.elapsedRealtime() - time}")
            viewRef.clear()
        }
    }

    override fun onApply(view: View, eventType: IntArray): Boolean {
        if (delayApply(view, eventType)) {
            viewRef[view] = Unit
            return true
        }
        return false
    }

    /**
     * @return true 延迟换肤；false 不延迟，立即执行换肤
     */
    protected open fun delayApply(view: View, eventType: IntArray): Boolean {
        return delayApplyStrategy?.invoke(view, eventType) ?: false
    }

    /**
     * 销毁
     */
    open fun destroy() {
        if (AttrApplyManager.onApplyInterceptor == this) {
            AttrApplyManager.onApplyInterceptor = null
        }
        SkinManager.removeSkinChangeListener(this)
    }
}
