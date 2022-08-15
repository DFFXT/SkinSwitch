package com.skin.skincore.loader

import android.content.Context
import android.content.ContextWrapper
import android.view.ContextThemeWrapper
import com.skin.skincore.AutoContext
import com.skin.skincore.inflater.InflaterInterceptor
import com.skin.skincore.reflex.switchBaseContext
import java.lang.ref.WeakReference

/**
 * 对context进行缓存
 */
class ContextLoader(context: Context) {
    //private val autoContext = AutoContext((context as ContextWrapper).baseContext)
    init {
        InflaterInterceptor.addInterceptor(context)
    }
    val ctxRef = WeakReference(context)

    /**
     * 销毁该loader，进行资源释放
     */
    fun destroy() {
    }
}
