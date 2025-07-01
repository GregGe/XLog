package com.logger.xlog.formatter.message.throwable

import com.logger.xlog.internal.util.StackTraceUtil

/**
 * Simply put each stack trace(method name, source file and line number) of the throwable
 * in a single line.
 */
class DefaultThrowableFormatter : ThrowableFormatter {
    override fun format(data: Throwable): String {
        return StackTraceUtil.getStackTraceString(data)
    }
}
