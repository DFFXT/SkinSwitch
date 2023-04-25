package com.skin.log

interface ILogger {

    fun v(tag: String, msg: String?)
    fun d(tag: String, msg: String?)
    fun i(tag: String, msg: String?)
    fun e(tag: String, msg: String?)
}
