package com.logger.xlog.interceptor

import com.logger.xlog.LogItem

/**
 * An filter interceptor is used to filter some specific logs out, this filtered logs won't be
 * printed by any printer.
 *
 * 
 */
abstract class AbstractFilterInterceptor : Interceptor {
    /**
     * {@inheritDoc}
     *
     * @param log the original log
     * @return the original log if it is acceptable, or null if it should be filtered out
     */
    override fun intercept(log: LogItem): LogItem? {
        if (reject(log)) {
            // Filter this log out.
            return null
        }
        return log
    }

    /**
     * Whether specific log should be filtered out.
     *
     * @param log the specific log
     * @return true if the log should be filtered out, false otherwise
     */
    protected abstract fun reject(log: LogItem): Boolean
}
