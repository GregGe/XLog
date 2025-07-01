package com.logger.xlog

/**
 * Represent a single log going to be printed.
 *
 *
 */
class LogItem {
    /**
     * Level of the log.
     *
     * @see LogLevel
     */
    @JvmField
    var level: Int

    /**
     * The tag, should not be null.
     */
    @JvmField
    var tag: String?

    /**
     * The formatted message, should not be null.
     */
    @JvmField
    var msg: String?

    /**
     * The formatted thread info, null if thread info is disabled.
     *
     * @see ILoggerConfig.enableThreadInfo
     * @see ILoggerConfig.disableThreadInfo
     */
    var threadInfo: String? = null

    /**
     * The formatted stack trace info, null if stack trace info is disabled.
     *
     * @see ILoggerConfig.enableStackTrace
     * @see ILoggerConfig.disableStackTrace
     */
    var stackTraceInfo: String? = null

    constructor(level: Int, tag: String, msg: String?) {
        this.level = level
        this.tag = tag
        this.msg = msg
    }

    constructor(
        level: Int,
        tag: String,
        threadInfo: String?,
        stackTraceInfo: String?,
        msg: String?
    ) {
        this.level = level
        this.tag = tag
        this.threadInfo = threadInfo
        this.stackTraceInfo = stackTraceInfo
        this.msg = msg
    }

    override fun toString(): String {
        return "LogItem(level=$level, tag=$tag, msg=$msg, threadInfo=$threadInfo, stackTraceInfo=$stackTraceInfo)"
    }
}
