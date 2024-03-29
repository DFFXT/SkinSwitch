package com.skin.log

import android.util.Log

/**
 * 默认日志输出
 */
internal class DefaultLogger : ILogger {
    override fun v(tag: String, msg: String?) {
        Log.v(tag, msg ?: "")
    }
    override fun d(tag: String, msg: String?) {
        Log.d(tag, msg ?: "")
    }

    override fun i(tag: String, msg: String?) {
        Log.i(tag, msg ?: "")
    }

    override fun e(tag: String, msg: String?) {
        Log.e(tag, msg ?: "")
    }
}
