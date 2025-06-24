package com.logger.xlog.formatter.thread

/**
 * Formatted stack trace looks like:
 * <br></br>Thread: thread-name
 */
class DefaultThreadFormatter : ThreadFormatter {
    override fun format(data: Thread?): String {
        return "Thread: ${data?.name ?: "null"}"
    }
}
