package com.skin.skincore.plug

import android.os.Handler
import android.os.Looper
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
        // 默认可以拦截刚创建的view的换肤
        var canInterceptOnCreatedView = true
    }
    protected val handler = Handler(Looper.getMainLooper())

    // 需要延迟换肤的view
    protected val viewRef = WeakHashMap<View, Unit>()

    fun init() {
        SkinManager.addSkinChangeListener(this)
        AttrApplyManager.onApplyInterceptor = this
    }

    override fun onThemeChanged(theme: Int, isNight: Boolean, eventType: IntArray) {
        handler.post {
            Logger.d("SkinFragmentSpeedUp", "onThemeChange delay start")
            val event = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)
            for (ref in viewRef) {
                val v = ref.key
                val union = v.getViewUnion() ?: continue
                AttrApplyManager.apply(event, v, union, SkinManager.getResourceProvider(v.context))
            }
            Logger.d("SkinFragmentSpeedUp", "onThemeChange delay finish ${viewRef.size}")
            viewRef.clear()
        }
    }

    override fun onApply(view: View, eventType: IntArray): Boolean {
        if (delayApply(view, eventType) && canInterceptOnCreatedView) {
            // 拦截未显示的view，而且不是view刚创建, 如果是刚创建的View，则不加入map
            if (!eventType.contains(BaseViewApply.EVENT_TYPE_CREATE)) {
                viewRef[view] = Unit
            }
            return true
        }
        return false
    }

    // 当不再viewTree上，而且不是创建，则拦截，延迟换肤
    protected open fun delayApply(view: View, eventType: IntArray): Boolean {
        return !view.isAttachedToWindow && !eventType.contains(BaseViewApply.EVENT_TYPE_CREATE)
    }
}
