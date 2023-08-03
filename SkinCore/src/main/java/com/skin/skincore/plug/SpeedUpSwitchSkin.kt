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
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * 换肤提速
 * 对于不可见的view，延迟换肤，post一下再换肤
 */
open class SpeedUpSwitchSkin : AttrApplyManager.AttrApplyInterceptor, OnThemeChangeListener {
    protected val handler = Handler(Looper.getMainLooper())

    // 需要延迟换肤的view
    protected val viewRef = LinkedList<WeakReference<View>>()

    fun init() {
        SkinManager.addSkinChangeListener(this)
        AttrApplyManager.onApplyInterceptor = this
    }

    override fun onThemeChanged(theme: Int, isNight: Boolean, eventType: IntArray) {
        handler.post {
            Logger.d("SkinFragmentSpeedUp", "onThemeChange delay start")
            val event = intArrayOf(BaseViewApply.EVENT_TYPE_THEME)
            for (ref in viewRef) {
                val v = ref.get() ?: continue
                val union = v.getViewUnion() ?: continue
                AttrApplyManager.apply(event, v, union, SkinManager.getResourceProvider(v.context))
            }
            Logger.d("SkinFragmentSpeedUp", "onThemeChange delay finish ${viewRef.size}")
            viewRef.clear()
        }
    }

    override fun onApply(view: View, eventType: IntArray): Boolean {
        if (delayApply(view, eventType)) {
            // 拦截未显示的view，而且不是view刚创建
            viewRef.add(WeakReference(view))
            return true
        }
        return false
    }

    protected open fun delayApply(view: View, eventType: IntArray): Boolean {
        return !view.isAttachedToWindow
    }
}
