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

interface ILoggerConfig {
    var loggerParams: LoggerParams
    /**
     * Set the log level, the logs below of which would not be printed.
     *
     * @param logLevel the log level
     *
     */
    fun setLogLevel(logLevel: Int): ILoggerConfig

    /**
     * Set the tag string used when log.
     *
     * @param tag the tag string used when log
     */
    fun setTag(tag: String): ILoggerConfig

    /**
     * Enable thread info, the thread info would be printed with the log message.
     */
    fun enableThreadInfo(): ILoggerConfig

    /**
     * Disable thread info, the thread info won't be printed with the log message.
     */
    fun disableThreadInfo(): ILoggerConfig

    /**
     * Enable stack trace, the stack trace would be printed with the log message.
     *
     * @param depth the number of stack trace elements we should log, 0 if no limitation
     *
     * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log when
     * logging with stack trace, it can be a package name like
     * "com.logger.xlog", a class name like "com.yourdomain.logWrapper",
     * or something else between package name and class name, like "com.yourdomain.".
     * It is mostly used when you are using a logger wrapper
     *
     * @see StackTraceFormatter
     */
    fun enableStackTrace(depth: Int, stackTraceOrigin: String? = null): ILoggerConfig

    /**
     * Disable stack trace, the stack trace won't be printed with the log message.
     *
     * @see StackTraceFormatter
     */
    fun disableStackTrace(): ILoggerConfig

    /**
     * Enable border, the border would surround the entire log content, and separate the log
     * message, thread info and stack trace.
     *
     * @see BorderFormatter
     */
    fun enableBorder(): ILoggerConfig

    /**
     * Disable border, the log content won't be surrounded by a border.
     */
    fun disableBorder(): ILoggerConfig

    /**
     * Set the JSON formatter when [Logger] log a JSON string.
     *
     * @param jsonFormatter the JSON formatter when [Logger] log a JSON string
     */
    fun jsonFormatter(jsonFormatter: JsonFormatter?): ILoggerConfig

    /**
     * Set the XML formatter when [Logger] log a XML string.
     *
     * @param xmlFormatter the XML formatter when [Logger] log a XML string
     */
    fun xmlFormatter(xmlFormatter: XmlFormatter?): ILoggerConfig

    /**
     * Set the throwable formatter when [Logger] log a message with throwable.
     *
     * @param throwableFormatter the throwable formatter when [Logger] log a message with
     * throwable
     */
    fun throwableFormatter(throwableFormatter: ThrowableFormatter?): ILoggerConfig

    /**
     * Set the thread formatter when [Logger] logging.
     *
     * @param threadFormatter the thread formatter when [Logger] logging
     */
    fun threadFormatter(threadFormatter: ThreadFormatter?): ILoggerConfig

    /**
     * Set the stack trace formatter when [Logger] logging.
     *
     * @param stackTraceFormatter the stace trace formatter when [Logger] logging
     */
    fun stackTraceFormatter(stackTraceFormatter: StackTraceFormatter?): ILoggerConfig


    /**
     * Set the border formatter when [Logger] logging.
     *
     * @param borderFormatter the border formatter when [Logger] logging
     */
    fun borderFormatter(borderFormatter: BorderFormatter?): ILoggerConfig

    /**
     * Add an object formatter for specific class of object when [Logger] log an object.
     *
     * @param objectClass     the class of object
     * @param objectFormatter the object formatter to add
     * @param <T>             the type of object
     *
     */
    fun <R> addObjectFormatter(
        objectClass: Class<R>, objectFormatter: ObjectFormatter<in R>
    ): ILoggerConfig

    /**
     * Copy all object formatters, only for internal usage.
     *
     * @param objectFormatters the object formatters to copy
     */
    fun objectFormatters(objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>): ILoggerConfig

    /**
     * Add an interceptor when [Logger] logging.
     *
     * @param interceptor the intercept to add
     *
     */
    fun addInterceptor(interceptor: Interceptor): ILoggerConfig

    /**
     * Copy all interceptors, only for internal usage.
     *
     * @param interceptors the interceptors to copy
     */
    fun interceptors(interceptors: MutableList<Interceptor>?): ILoggerConfig

    /**
     * Set the printers used to print the log when [Logger] log.
     *
     * @param printers the printers used to print the log when [Logger] log
     * @return self
     */
    fun printers(vararg printers: Printer): ILoggerConfig

    /**
     * Init empty fields with default values
     */
    fun initEmptyFieldsWithDefaultValues()

    fun updateLoggerConfig(newConfig: ILoggerConfig): ILoggerConfig {
        newConfig.loggerParams.apply {
            loggerParams.tag = tag
            loggerParams.logLevel = logLevel
            loggerParams.withThread = withThread
            loggerParams.withStackTrace = withStackTrace
            loggerParams.stackTraceOrigin = stackTraceOrigin
            loggerParams.stackTraceDepth = stackTraceDepth
            loggerParams.withBorder = withBorder

            jsonFormatter?.let {
                loggerParams.jsonFormatter = it
            }

            xmlFormatter?.let {
                loggerParams.xmlFormatter = it
            }

            throwableFormatter?.let {
                loggerParams.throwableFormatter = it
            }

            threadFormatter?.let {
                loggerParams.threadFormatter = it
            }

            stackTraceFormatter?.let {
                loggerParams.stackTraceFormatter = it
            }

            borderFormatter?.let {
                loggerParams.borderFormatter = it
            }

            objectFormatters?.let {
                loggerParams.objectFormatters = it
            }

            interceptors?.let {
                loggerParams.interceptors = it
            }

            printer?.let {
                loggerParams.printer = it
            }
        }
        return this
    }
}