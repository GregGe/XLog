package com.logger.xlog

import com.logger.xlog.Logger.Builder
import com.logger.xlog.XLog.assertInitialization
import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.interceptor.Interceptor
import com.logger.xlog.internal.DefaultsFactory.builtinObjectFormatters
import com.logger.xlog.internal.Platform.Companion.get
import com.logger.xlog.internal.SystemCompat
import com.logger.xlog.internal.util.StackTraceUtil
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.PrinterSet

/**
 * A logger is used to do the real logging work, can use multiple log printers to print the log.
 *
 *
 * A [Logger] is always generated and mostly accessed by [XLog], but for customization
 * purpose, you can configure a [Logger] via the [Builder] which is returned by
 * [XLog] when you trying to start a customization using [XLog.tag]
 * or other configuration method, and to use the customized [Logger], you should call
 * the [Builder.build] to build a [Logger], and then you can log using
 * the [Logger] assuming that you are using the [XLog] directly.
 */
class Logger {
    /**
     * The log configuration which you should respect to when logging.
     */
    private var logConf: LogConfiguration

    /**
     * The log printer used to print the logs.
     */
    private var printer: Printer? = null

    /**
     * Construct a logger.
     *
     * @param logConfiguration the log configuration which you should respect to when logging
     * @param printer          the log printer used to print the log
     */
    internal constructor(logConfiguration: LogConfiguration, printer: Printer?) {
        this.logConf = logConfiguration
        this.printer = printer
    }

    /**
     * Construct a logger using builder.
     *
     * @param builder the logger builder
     */
    internal constructor(builder: Builder) {
        val logConfigBuilder = LogConfiguration.Builder(
            XLog.sLogConfiguration
        )

        if (builder.logLevel != 0) {
            logConfigBuilder.logLevel(builder.logLevel)
        }

        if (builder.tag != null) {
            logConfigBuilder.tag(builder.tag!!)
        }

        if (builder.threadSet) {
            if (builder.withThread) {
                logConfigBuilder.enableThreadInfo()
            } else {
                logConfigBuilder.disableThreadInfo()
            }
        }
        if (builder.stackTraceSet) {
            if (builder.withStackTrace) {
                logConfigBuilder.enableStackTrace(builder.stackTraceOrigin, builder.stackTraceDepth)
            } else {
                logConfigBuilder.disableStackTrace()
            }
        }
        if (builder.borderSet) {
            if (builder.withBorder) {
                logConfigBuilder.enableBorder()
            } else {
                logConfigBuilder.disableBorder()
            }
        }

        if (builder.jsonFormatter != null) {
            logConfigBuilder.jsonFormatter(builder.jsonFormatter)
        }
        if (builder.xmlFormatter != null) {
            logConfigBuilder.xmlFormatter(builder.xmlFormatter)
        }
        if (builder.throwableFormatter != null) {
            logConfigBuilder.throwableFormatter(builder.throwableFormatter)
        }
        if (builder.threadFormatter != null) {
            logConfigBuilder.threadFormatter(builder.threadFormatter)
        }
        if (builder.stackTraceFormatter != null) {
            logConfigBuilder.stackTraceFormatter(builder.stackTraceFormatter)
        }
        if (builder.borderFormatter != null) {
            logConfigBuilder.borderFormatter(builder.borderFormatter)
        }
        if (builder.objectFormatters != null) {
            logConfigBuilder.objectFormatters(builder.objectFormatters!!)
        }
        if (builder.interceptors != null) {
            logConfigBuilder.interceptors(builder.interceptors)
        }
        logConf = logConfigBuilder.build()

        printer = if (builder.printer != null) {
            builder.printer
        } else {
            XLog.sPrinter
        }
    }

    /**
     * Log an object with level [LogLevel.VERBOSE].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun v(obj: Any?) {
        println(LogLevel.VERBOSE, obj)
    }

    /**
     * Log an array with level [LogLevel.VERBOSE].
     *
     * @param array the array to log
     */
    fun v(array: Array<Any>) {
        println(LogLevel.VERBOSE, array)
    }

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun v(format: String?, vararg args: Any) {
        println(LogLevel.VERBOSE, format, *args)
    }

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     */
    fun v(msg: String?) {
        println(LogLevel.VERBOSE, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun v(msg: String?, tr: Throwable) {
        println(LogLevel.VERBOSE, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.DEBUG].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun d(obj: Any?) {
        println(LogLevel.DEBUG, obj)
    }

    /**
     * Log an array with level [LogLevel.DEBUG].
     *
     * @param array the array to log
     */
    fun d(array: Array<Any>) {
        println(LogLevel.DEBUG, array)
    }

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    fun d(format: String?, vararg args: Any) {
        println(LogLevel.DEBUG, format, *args)
    }

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     */
    fun d(msg: String?) {
        println(LogLevel.DEBUG, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun d(msg: String?, tr: Throwable) {
        println(LogLevel.DEBUG, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.INFO].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun i(obj: Any?) {
        println(LogLevel.INFO, obj)
    }

    /**
     * Log an array with level [LogLevel.INFO].
     *
     * @param array the array to log
     */
    fun i(array: Array<Any>) {
        println(LogLevel.INFO, array)
    }

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    fun i(format: String?, vararg args: Any) {
        println(LogLevel.INFO, format, *args)
    }

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param msg the message to log
     */
    fun i(msg: String?) {
        println(LogLevel.INFO, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.INFO].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun i(msg: String?, tr: Throwable) {
        println(LogLevel.INFO, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.WARN].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun w(obj: Any?) {
        println(LogLevel.WARN, obj)
    }

    /**
     * Log an array with level [LogLevel.WARN].
     *
     * @param array the array to log
     */
    fun w(array: Array<Any>) {
        println(LogLevel.WARN, array)
    }

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    fun w(format: String?, vararg args: Any) {
        println(LogLevel.WARN, format, *args)
    }

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param msg the message to log
     */
    fun w(msg: String?) {
        println(LogLevel.WARN, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.WARN].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun w(msg: String?, tr: Throwable) {
        println(LogLevel.WARN, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ERROR].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun e(obj: Any?) {
        println(LogLevel.ERROR, obj)
    }

    /**
     * Log an array with level [LogLevel.ERROR].
     *
     * @param array the array to log
     */
    fun e(array: Array<Any>) {
        println(LogLevel.ERROR, array)
    }

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    fun e(format: String?, vararg args: Any) {
        println(LogLevel.ERROR, format, *args)
    }

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     */
    fun e(msg: String?) {
        println(LogLevel.ERROR, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun e(msg: String?, tr: Throwable) {
        println(LogLevel.ERROR, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ASSERT].
     *
     * @param obj the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun wtf(obj: Any?) {
        println(LogLevel.ASSERT, obj)
    }

    /**
     * Log an array with level [LogLevel.ASSERT].
     *
     * @param array the array to log
     */
    fun wtf(array: Array<Any>) {
        println(LogLevel.ASSERT, array)
    }

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param format the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    fun wtf(format: String?, vararg args: Any) {
        println(LogLevel.ASSERT, format, *args)
    }

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     */
    fun wtf(msg: String?) {
        println(LogLevel.ASSERT, msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun wtf(msg: String?, tr: Throwable) {
        println(LogLevel.ASSERT, msg, tr)
    }

    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param obj   the object to log
     * @see Builder.addObjectFormatter
     * 
     */
    fun log(logLevel: Int, obj: Any?) {
        println(logLevel, obj)
    }

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log
     * 
     */
    fun log(logLevel: Int, array: Array<Any>) {
        println(logLevel, array)
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param format   the format of the message to log, null if just need to concat arguments
     * @param args     the arguments of the message to log
     * 
     */
    fun log(logLevel: Int, format: String?, vararg args: Any) {
        println(logLevel, format, *args)
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * 
     */
    fun log(logLevel: Int, msg: String?) {
        println(logLevel, msg)
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     * 
     */
    fun log(logLevel: Int, msg: String?, tr: Throwable) {
        println(logLevel, msg, tr)
    }

    /**
     * Log a JSON string, with level [LogLevel.DEBUG] by default.
     *
     * @param json the JSON string to log
     */
    fun json(json: String, logLevel: Int = LogLevel.DEBUG) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, logConf.jsonFormatter!!.format(json))
    }

    /**
     * Log a XML string, with level [LogLevel.DEBUG] by default.
     *
     * @param xml the XML string to log
     */
    fun xml(xml: String, logLevel: Int = LogLevel.DEBUG) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, logConf.xmlFormatter!!.format(xml))
    }

    /**
     * Print an object in a new line.
     *
     * @param logLevel the log level of the printing object
     * @param obj   the object to print
     */
    private fun <T> println(logLevel: Int, obj: T?) {
        if (logLevel < logConf.logLevel) {
            return
        }
        val objectString: String
        if (obj != null) {
            val objectFormatter: ObjectFormatter<in T>? = logConf.getObjectFormatter(obj)
            objectString = objectFormatter?.format(obj) ?: obj.toString()
        } else {
            objectString = "null"
        }
        printlnInternal(logLevel, objectString)
    }

    /**
     * Print an array in a new line.
     *
     * @param logLevel the log level of the printing array
     * @param array    the array to print
     */
    private fun println(logLevel: Int, array: Array<Any>) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, array.contentDeepToString())
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param format   the format of the printing log, null if just need to concat arguments
     * @param args     the arguments of the printing log
     */
    private fun println(logLevel: Int, format: String?, vararg args: Any) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, formatArgs(format, *args))
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     *//*package*/
    fun println(logLevel: Int, msg: String?) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, msg)
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     * @param tr       a throwable object to log
     */
    private fun println(logLevel: Int, msg: String?, tr: Throwable) {
        if (logLevel < logConf.logLevel) {
            return
        }
        printlnInternal(logLevel, msg.safePlus() + logConf.throwableFormatter!!.format(tr))
    }

    /**
     * Print a log in a new line internally.
     *
     * @param logLevel the log level of the printing log
     * @param message      the message you would like to log
     */
    private fun printlnInternal(logLevel: Int, message: String?) {
        var logLvl = logLevel
        var msg = message
        var tag = logConf.tag
        var thread = if (logConf.withThread) {
            logConf.threadFormatter!!.format(Thread.currentThread())
        } else {
            null
        }
        var stackTrace = if (logConf.withStackTrace) {
            logConf.stackTraceFormatter!!.format(
                StackTraceUtil.getCroppedRealStackTrack(
                    Throwable().stackTrace,
                    logConf.stackTraceOrigin,
                    logConf.stackTraceDepth
                )
            )
        } else {
            null
        }

        if (logConf.interceptors != null) {
            var log = LogItem(logLvl, tag, thread, stackTrace, msg)
            for (interceptor in logConf.interceptors!!) {
                // if log is null, Log is eaten, don't print this log.
                log = interceptor.intercept(log) ?: return

                // Check if the log still healthy.
                if (log.tag == null || log.msg == null) {
                    get().error(("Interceptor $interceptor should not remove the tag or message of a log, if you don't want to print this log, just return a null when intercept."))
                    return
                }
            }

            // Use fields after interception.
            logLvl = log.level
            tag = log.tag.toString()
            thread = log.threadInfo
            stackTrace = log.stackTraceInfo
            msg = log.msg
        }

        val finalMessage = if (logConf.withBorder) {
            logConf.borderFormatter!!.format(arrayOf(thread, stackTrace, msg)) ?: ""
        } else {
            "${thread.safePlus()}${stackTrace.safePlus()}$msg"
        }

        printer!!.println(logLvl, tag, finalMessage)
    }

    /**
     * Format a string with arguments.
     *
     * @param format the format string, null if just to concat the arguments
     * @param args   the arguments
     * @return the formatted string
     */
    private fun formatArgs(format: String?, vararg args: Any): String {
        if (format != null) {
            return String.format(format, *args)
        } else {
            val sb = StringBuilder()
            var i = 0
            val size = args.size
            while (i < size) {
                if (i != 0) {
                    sb.append(", ")
                }
                sb.append(args[i])
                i++
            }
            return sb.toString()
        }
    }

    /**
     * Builder for [Logger].
     */
    class Builder {
        /**
         * The log level, the logs below of which would not be printed.
         */
        var logLevel: Int = 0

        /**
         * The tag string when [Logger] log.
         */
        var tag: String? = null

        /**
         * Whether we should log with thread info.
         */
        var withThread: Boolean = false

        /**
         * Whether we have enabled/disabled thread info.
         */
        var threadSet: Boolean = false

        /**
         * Whether we should log with stack trace.
         */
        var withStackTrace: Boolean = false

        /**
         * The origin of stack trace elements from which we should NOT log when logging with stack trace,
         * it can be a package name like "com.logger.xlog", a class name like "com.yourdomain.logWrapper",
         * or something else between package name and class name, like "com.yourdomain.".
         *
         *
         * It is mostly used when you are using a logger wrapper.
         */
        var stackTraceOrigin: String? = null

        /**
         * The number of stack trace elements we should log when logging with stack trace,
         * 0 if no limitation.
         */
        var stackTraceDepth: Int = 0

        /**
         * Whether we have enabled/disabled stack trace.
         */
        var stackTraceSet: Boolean = false

        /**
         * Whether we should log with border.
         */
        var withBorder: Boolean = false

        /**
         * Whether we have enabled/disabled border.
         */
        var borderSet: Boolean = false

        /**
         * The JSON formatter when [Logger] log a JSON string.
         */
        var jsonFormatter: JsonFormatter? = null

        /**
         * The XML formatter when [Logger] log a XML string.
         */
        var xmlFormatter: XmlFormatter? = null

        /**
         * The throwable formatter when [Logger] log a message with throwable.
         */
        var throwableFormatter: ThrowableFormatter? = null

        /**
         * The thread formatter when [Logger] logging.
         */
        var threadFormatter: ThreadFormatter? = null

        /**
         * The stack trace formatter when [Logger] logging.
         */
        var stackTraceFormatter: StackTraceFormatter? = null

        /**
         * The border formatter when [Logger] logging.
         */
        var borderFormatter: BorderFormatter? = null

        /**
         * The object formatters, used when [Logger] logging an object.
         */
        var objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>? = null

        /**
         * The intercepts, used when [Logger] logging.
         */
        var interceptors: MutableList<Interceptor>? = null

        /**
         * The printer used to print the log when [Logger] log.
         */
        var printer: Printer? = null

        /**
         * Construct a builder, which will perform the same as the global one by default.
         */
        init {
            assertInitialization()
        }

        /**
         * Set the log level, the logs below of which would not be printed.
         *
         * @param logLevel the log level
         * @return the builder
         * 
         */
        fun logLevel(logLevel: Int): Builder {
            this.logLevel = logLevel
            return this
        }

        /**
         * Set the tag string when [Logger] log.
         *
         * @param tag the tag string when [Logger] log
         * @return the builder
         */
        fun tag(tag: String?): Builder {
            this.tag = tag
            return this
        }

        /**
         * Enable thread info, the thread info would be printed with the log message.
         *
         * @return the builder
         * @see ThreadFormatter
         *
         * 
         */
        fun enableThreadInfo(): Builder {
            this.withThread = true
            this.threadSet = true
            return this
        }

        /**
         * Disable thread info, the thread info won't be printed with the log message.
         *
         * @return the builder
         * 
         */
        fun disableThreadInfo(): Builder {
            this.withThread = false
            this.threadSet = true
            return this
        }

        /**
         * Enable stack trace, the stack trace would be printed with the log message.
         *
         * @param depth the number of stack trace elements we should log, 0 if no limitation
         * @return the builder
         * @see StackTraceFormatter
         */
        fun enableStackTrace(depth: Int): Builder {
            this.withStackTrace = true
            this.stackTraceDepth = depth
            this.stackTraceSet = true
            return this
        }

        /**
         * Enable stack trace, the stack trace would be printed with the log message.
         *
         * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log when
         * logging with stack trace, it can be a package name like
         * "com.logger.xlog", a class name like "com.yourdomain.logWrapper",
         * or something else between package name and class name, like "com.yourdomain.".
         * It is mostly used when you are using a logger wrapper
         * @param depth            the number of stack trace elements we should log, 0 if no limitation
         * @return the builder
         * @see StackTraceFormatter
         *
         */
        fun enableStackTrace(stackTraceOrigin: String?, depth: Int): Builder {
            this.withStackTrace = true
            this.stackTraceOrigin = stackTraceOrigin
            this.stackTraceDepth = depth
            this.stackTraceSet = true
            return this
        }

        /**
         * Disable stack trace, the stack trace won't be printed with the log message.
         *
         * @return the builder
         * @see StackTraceFormatter
         */
        fun disableStackTrace(): Builder {
            this.withStackTrace = false
            this.stackTraceOrigin = null
            this.stackTraceDepth = 0
            this.stackTraceSet = true
            return this
        }

        /**
         * Enable border, the border would surround the entire log content, and separate the log
         * message, thread info and stack trace.
         *
         * @return the builder
         * @see BorderFormatter
         *
         */
        fun enableBorder(): Builder {
            this.withBorder = true
            this.borderSet = true
            return this
        }


        /**
         * Disable border, the log content won't be surrounded by a border.
         *
         * @return the builder
         */
        fun disableBorder(): Builder {
            this.withBorder = false
            this.borderSet = true
            return this
        }

        /**
         * Set the JSON formatter when [Logger] log a JSON string.
         *
         * @param jsonFormatter the JSON formatter when [Logger] log a JSON string
         * @return the builder
         */
        fun jsonFormatter(jsonFormatter: JsonFormatter?): Builder {
            this.jsonFormatter = jsonFormatter
            return this
        }

        /**
         * Set the XML formatter when [Logger] log a XML string.
         *
         * @param xmlFormatter the XML formatter when [Logger] log a XML string
         * @return the builder
         */
        fun xmlFormatter(xmlFormatter: XmlFormatter?): Builder {
            this.xmlFormatter = xmlFormatter
            return this
        }

        /**
         * Set the throwable formatter when [Logger] log a message with throwable.
         *
         * @param throwableFormatter the throwable formatter when [Logger] log a message with
         * throwable
         * @return the builder
         */
        fun throwableFormatter(throwableFormatter: ThrowableFormatter?): Builder {
            this.throwableFormatter = throwableFormatter
            return this
        }

        /**
         * Set the thread formatter when [Logger] logging.
         *
         * @param threadFormatter the thread formatter when [Logger] logging
         * @return the builder
         */
        fun threadFormatter(threadFormatter: ThreadFormatter?): Builder {
            this.threadFormatter = threadFormatter
            return this
        }

        /**
         * Set the stack trace formatter when [Logger] logging.
         *
         * @param stackTraceFormatter the stace trace formatter when [Logger] logging
         * @return the builder
         */
        fun stackTraceFormatter(stackTraceFormatter: StackTraceFormatter?): Builder {
            this.stackTraceFormatter = stackTraceFormatter
            return this
        }

        /**
         * Set the border formatter when [Logger] logging.
         *
         * @param borderFormatter the border formatter when [Logger] logging
         * @return the builder
         */
        fun borderFormatter(borderFormatter: BorderFormatter?): Builder {
            this.borderFormatter = borderFormatter
            return this
        }

        /**
         * Add an object formatter for specific class of object when [Logger] log an object.
         *
         * @param objectClass     the class of object
         * @param objectFormatter the object formatter to add
         * @param <T>             the type of object
         * @return the builder
         * 
        </T> */
        fun <T> addObjectFormatter(
            objectClass: Class<T>, objectFormatter: ObjectFormatter<in T>
        ): Builder {
            if (objectFormatters == null) {
                objectFormatters = builtinObjectFormatters()
            }
            objectFormatters!![objectClass] = objectFormatter
            return this
        }

        /**
         * Add an interceptor when [Logger] logging.
         *
         * @param interceptor the intercept to add
         * @return the builder
         * 
         */
        fun addInterceptor(interceptor: Interceptor): Builder {
            if (interceptors == null) {
                interceptors = mutableListOf()
            }
            interceptors!!.add(interceptor)
            return this
        }

        /**
         * Set the printers used to print the log when [Logger] log.
         *
         * @param printers the printers used to print the log when [Logger] log
         * @return the builder
         */
        fun printers(vararg printers: Printer): Builder {
            if (printers.isEmpty()) {
                // Is there anybody want to reuse the Builder? It's not a good idea, but
                // anyway, in case you want to reuse a builder and do not want the custom
                // printers anymore, just do it.
                this.printer = null
            } else if (printers.size == 1) {
                this.printer = printers[0]
            } else {
                this.printer = PrinterSet(*printers)
            }
            return this
        }

        /**
         * Convenience of [.build] and [Logger.v].
         *
         * 
         */
        fun v(obj: Any?) {
            build().v(obj)
        }

        /**
         * Convenience of [.build] and [Logger.v].
         *
         * 
         */
        fun v(array: Array<Any>) {
            build().v(array)
        }

        /**
         * Convenience of [.build] and [Logger.v].
         */
        fun v(format: String?, vararg args: Any) {
            build().v(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.v].
         */
        fun v(msg: String?) {
            build().v(msg)
        }

        /**
         * Convenience of [.build] and [Logger.v].
         */
        fun v(msg: String?, tr: Throwable) {
            build().v(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.d].
         *
         * 
         */
        fun d(obj: Any?) {
            build().d(obj)
        }

        /**
         * Convenience of [.build] and [Logger.d].
         *
         * 
         */
        fun d(array: Array<Any>) {
            build().d(array)
        }

        /**
         * Convenience of [.build] and [Logger.d].
         */
        fun d(format: String?, vararg args: Any) {
            build().d(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.d].
         */
        fun d(msg: String?) {
            build().d(msg)
        }

        /**
         * Convenience of [.build] and [Logger.d].
         */
        fun d(msg: String?, tr: Throwable) {
            build().d(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.i].
         *
         * 
         */
        fun i(obj: Any?) {
            build().i(obj)
        }

        /**
         * Convenience of [.build] and [Logger.i].
         *
         * 
         */
        fun i(array: Array<Any>) {
            build().i(array)
        }

        /**
         * Convenience of [.build] and [Logger.i].
         */
        fun i(format: String?, vararg args: Any) {
            build().i(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.i].
         */
        fun i(msg: String?) {
            build().i(msg)
        }

        /**
         * Convenience of [.build] and [Logger.i].
         */
        fun i(msg: String?, tr: Throwable) {
            build().i(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.w].
         *
         * 
         */
        fun w(obj: Any?) {
            build().w(obj)
        }

        /**
         * Convenience of [.build] and [Logger.w].
         *
         * 
         */
        fun w(array: Array<Any>) {
            build().w(array)
        }

        /**
         * Convenience of [.build] and [Logger.w].
         */
        fun w(format: String?, vararg args: Any) {
            build().w(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.w].
         */
        fun w(msg: String?) {
            build().w(msg)
        }

        /**
         * Convenience of [.build] and [Logger.w].
         */
        fun w(msg: String?, tr: Throwable) {
            build().w(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.e].
         *
         * 
         */
        fun e(obj: Any?) {
            build().e(obj)
        }

        /**
         * Convenience of [.build] and [Logger.e].
         *
         * 
         */
        fun e(array: Array<Any>) {
            build().e(array)
        }

        /**
         * Convenience of [.build] and [Logger.e].
         */
        fun e(format: String?, vararg args: Any) {
            build().e(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.e].
         */
        fun e(msg: String?) {
            build().e(msg)
        }

        /**
         * Convenience of [.build] and [Logger.e].
         */
        fun e(msg: String?, tr: Throwable) {
            build().e(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.wtf].
         *
         *
         */
        fun wtf(obj: Any?) {
            build().wtf(obj)
        }

        /**
         * Convenience of [.build] and [Logger.wtf].
         *
         *
         */
        fun wtf(array: Array<Any>) {
            build().wtf(array)
        }

        /**
         * Convenience of [.build] and [Logger.wtf].
         */
        fun wtf(format: String?, vararg args: Any) {
            build().wtf(format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.wtf].
         */
        fun wtf(msg: String?) {
            build().wtf(msg)
        }

        /**
         * Convenience of [.build] and [Logger.wtf].
         */
        fun wtf(msg: String?, tr: Throwable) {
            build().wtf(msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.log].
         *
         * 
         */
        fun log(logLevel: Int, obj: Any?) {
            build().log(logLevel, obj)
        }

        /**
         * Convenience of [.build] and [Logger.log].
         *
         * 
         */
        fun log(logLevel: Int, array: Array<Any>) {
            build().log(logLevel, array)
        }

        /**
         * Convenience of [.build] and [Logger.log].
         *
         * 
         */
        fun log(logLevel: Int, format: String?, vararg args: Any) {
            build().log(logLevel, format, *args)
        }

        /**
         * Convenience of [.build] and [Logger.log].
         *
         * 
         */
        fun log(logLevel: Int, msg: String?) {
            build().log(logLevel, msg)
        }

        /**
         * Convenience of [.build] and [Logger.log].
         *
         * 
         */
        fun log(logLevel: Int, msg: String?, tr: Throwable) {
            build().log(logLevel, msg, tr)
        }

        /**
         * Convenience of [.build] and [Logger.json].
         */
        fun json(json: String) {
            build().json(json)
        }

        /**
         * Convenience of [.build] and [Logger.xml].
         */
        fun xml(xml: String) {
            build().xml(xml)
        }

        /**
         * Builds configured [Logger] object.
         *
         * @return the built configured [Logger] object
         */
        fun build(): Logger {
            return Logger(this)
        }
    }

    private fun String?.safePlus(separator: String = SystemCompat.lineSeparator): String {
        return if (this != null) {
            "$this$separator"
        } else {
            ""
        }
    }
}
