package com.logger.xlog.flattener

/**
 * 用于将日志元素（日志时间毫秒数、级别、标签和消息）展平为单个 CharSequence 的展平器。
 *
 * 
 */
interface Flattener {

    /**
     * 展平日志。
     *
     * @param timeMillis 日志的时间毫秒数
     * @param logLevel 日志的级别
     * @param tag 日志的标签
     * @param message 日志的消息
     * @return 格式化后的最终日志 CharSequence
     */
    fun flatten(timeMillis: Long, logLevel: Int, tag: String, message: String): CharSequence
}