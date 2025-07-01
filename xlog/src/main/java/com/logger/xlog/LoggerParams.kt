package com.logger.xlog

import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.interceptor.Interceptor
import com.logger.xlog.printer.Printer

class LoggerParams {
    /**
     * The log level, the logs below of which would not be printed.
     */
    var logLevel: Int = DEFAULT_LOG_LEVEL

    /**
     * The tag string.
     */
    var tag: String = DEFAULT_TAG

    /**
     * Whether we should log with thread info.
     */
    var withThread: Boolean = false

    /**
     * Whether we should log with stack trace.
     */
    var withStackTrace: Boolean = false

    /**
     * The origin of stack trace elements from which we should not log when logging with stack trace,
     * it can be a package name like "com.logger.xlog", a class name like "com.your_domain.logWrapper",
     * or something else between package name and class name, like "com.your_domain.".
     *
     * It is mostly used when you are using a logger wrapper.
     *
     */
    var stackTraceOrigin: String? = null

    /**
     * The number of stack trace elements we should log when logging with stack trace,
     * 0 if no limitation.
     */
    var stackTraceDepth: Int = 0

    /**
     * Whether we should log with border.
     */
    var withBorder: Boolean = false

    /**
     * The JSON formatter used to format the JSON string when log a JSON string.
     */
    var jsonFormatter: JsonFormatter? = null

    /**
     * The XML formatter used to format the XML string when log a XML string.
     */
    var xmlFormatter: XmlFormatter? = null

    /**
     * The throwable formatter used to format the throwable when log a message with throwable.
     */
    var throwableFormatter: ThrowableFormatter? = null

    /**
     * The thread formatter used to format the thread when logging.
     */
    var threadFormatter: ThreadFormatter? = null

    /**
     * The stack trace formatter used to format the stack trace when logging.
     */
    var stackTraceFormatter: StackTraceFormatter? = null

    /**
     * The border formatter used to format the border when logging.
     */
    var borderFormatter: BorderFormatter? = null

    /**
     * The object formatters, used when logging an object.
     */
    var objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>? = null

    /**
     * The interceptors, used to intercept the log when logging.
     */
    var interceptors: MutableList<Interceptor>? = null

    /**
     * The printer used to print the log when {@link [Logger]} log.
     */
    var printer: Printer? = null

    companion object {
        const val DEFAULT_LOG_LEVEL = LogLevel.ALL

        const val DEFAULT_TAG = "XLog"
    }
}