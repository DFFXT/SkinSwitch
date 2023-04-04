package com.skin.skincore.asset

import android.content.Context

/**
 * 将Context转换成id，如果id不一样则会重建Resource对象
 */
open class ContextId {
    fun getId(context: Context): Int {
        // 如果分辨率变了，需要以新分辨率新建Resource对象
        return context.resources.displayMetrics.hashCode()
    }
}
