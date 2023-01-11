package com.skin.log

import android.util.Log

/**
 * 默认日志输出
 */
internal class DefaultLogger : ILogger {
    override fun d(tag: String, msg: String?) {
        Log.i(tag, msg ?: "")
    }

    override fun i(tag: String, msg: String?) {
        Log.i(tag, msg ?: "")
    }
}
