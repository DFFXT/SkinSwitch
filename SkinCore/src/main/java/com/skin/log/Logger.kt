package com.skin.log

/**
 * 换肤框架里面的日志工具
 */
object Logger : ILogger {
    private var logger: ILogger? = DefaultLogger()

    /**
     * 设置log外部实现
     */
    fun setLoggerImpl(logger: ILogger?) {
        this.logger = logger
    }

    override fun v(tag: String, msg: String?) {
        logger?.v(tag, msg)
    }

    override fun d(tag: String, msg: String?) {
        logger?.d(tag, msg)
    }

    override fun i(tag: String, msg: String?) {
        logger?.i(tag, msg)
    }

    override fun e(tag: String, msg: String?) {
        logger?.e(tag, msg)
    }
}
