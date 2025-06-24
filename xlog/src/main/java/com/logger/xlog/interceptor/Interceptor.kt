package com.logger.xlog.interceptor

import com.logger.xlog.LogItem

/**
 * Interceptors are used to intercept every log after formatting message, thread info and
 * stack trace info, and before printing, normally we can modify or drop the log.
 *
 *
 * Remember interceptors are ordered, which means earlier added interceptor will get the log
 * first.
 *
 *
 * If any interceptor remove the log(by returning null when [.intercept]),
 * then the interceptors behind that one won't receive the log, and the log won't be printed at all.
 *
 * @see com.logger.xlog.LogConfiguration.Builder.addInterceptor
 * @see com.logger.xlog.XLog.addInterceptor
 * 
 */
interface Interceptor {
    /**
     * Intercept the log.
     *
     * @param log the original log
     * @return the modified log, or null if the log should not be printed
     */
    fun intercept(log: LogItem): LogItem?
}
