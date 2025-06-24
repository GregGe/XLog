package com.logger.xlog

import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.interceptor.Interceptor
import com.logger.xlog.internal.DefaultsFactory
import com.logger.xlog.internal.Platform
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.PrinterSet

/**
 * A log tool which can be used in android or java, the most important feature is it can print the
 * logs to multiple place in the same time, such as android shell, console and file, you can
 * even print the log to the remote server if you want, all of these can be done just within one
 * calling.
 * <br>Also, XLog is very flexible, almost every component is replaceable.
 */
object XLog {

    /**
     * Global logger for all direct logging via [XLog].
     */
    private lateinit var sLogger: Logger

    /**
     * Global log configuration.
     */
    lateinit var sLogConfiguration: LogConfiguration
        private set

    /**
     * Global log printer.
     */
    lateinit var sPrinter: Printer
        private set

    var sIsInitialized = false
        internal set

    /**
     * Initialize log system, should be called only once.
     *
     */
    fun init() {
        init(LogConfiguration.Builder().build(), DefaultsFactory.createPrinter())
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     */
    fun init(logLevel: Int) {
        init(
            LogConfiguration.Builder().logLevel(logLevel).build(),
            DefaultsFactory.createPrinter()
        )
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     */
    fun init(logConfiguration: LogConfiguration) {
        init(logConfiguration, DefaultsFactory.createPrinter())
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param printers the printers, each log would be printed by all of the printers
     */
    fun init(vararg printers: Printer) {
        init(LogConfiguration.Builder().build(), *printers)
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     * @param printers the printers, each log would be printed by all of the printers
     */
    fun init(logLevel: Int, vararg printers: Printer) {
        init(LogConfiguration.Builder().logLevel(logLevel).build(), *printers)
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     * @param printers         the printers, each log would be printed by all of the printers
     */
    fun init(logConfiguration: LogConfiguration, vararg printers: Printer) {
        if (sIsInitialized) {
            Platform.get().warn("XLog is already initialized, do not initialize again")
        }
        sIsInitialized = true

        sLogConfiguration = logConfiguration

        sPrinter = PrinterSet(*printers)

        sLogger = Logger(sLogConfiguration, sPrinter)
    }

    /**
     * Throw an IllegalStateException if not initialized.
     */
    fun assertInitialization() {
        check(sIsInitialized) {
            "Do you forget to initialize XLog?"
        }
    }

    /**
     * Start to customize a [Logger] and set the log level.
     *
     * @param logLevel the log level to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun logLevel(logLevel: Int): Logger.Builder {
        return Logger.Builder().logLevel(logLevel)
    }

    /**
     * Start to customize a [Logger] and set the tag.
     *
     * @param tag the tag to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun tag(tag: String): XLog {
        sLogConfiguration.explicitTag.set(tag)
        return this
    }

    /**
     * Start to customize a [Logger] and enable thread info, the thread info would be printed
     * with the log message.
     *
     * @return the [Logger.Builder] to build the [Logger]
     * @see ThreadFormatter
     */
    fun enableThreadInfo(): Logger.Builder {
        return Logger.Builder().enableThreadInfo()
    }

    /**
     * Start to customize a [Logger] and disable thread info, the thread info won't be printed
     * with the log message.
     *
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun disableThreadInfo(): Logger.Builder {
        return Logger.Builder().disableThreadInfo()
    }

    /**
     * Start to customize a [Logger] and enable stack trace, the stack trace would be printed
     * with the log message.
     *
     * @param depth the number of stack trace elements we should log, 0 if no limitation
     * @return the [Logger.Builder] to build the [Logger]
     * @see StackTraceFormatter
     */
    fun enableStackTrace(depth: Int): Logger.Builder {
        return Logger.Builder().enableStackTrace(depth)
    }

    /**
     * Start to customize a [Logger] and enable stack trace, the stack trace would be printed
     * with the log message.
     *
     * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log,
     *                         it can be a package name like "com.logger.xlog", a class name
     *                         like "com.yourdomain.logWrapper", or something else between
     *                         package name and class name, like "com.yourdomain.".
     *                         It is mostly used when you are using a logger wrapper
     * @param depth            the number of stack trace elements we should log, 0 if no limitation
     * @return the [Logger.Builder] to build the [Logger]
     * @see StackTraceFormatter

     */
    fun enableStackTrace(stackTraceOrigin: String, depth: Int): Logger.Builder {
        return Logger.Builder().enableStackTrace(stackTraceOrigin, depth)
    }

    /**
     * Start to customize a [Logger] and disable stack trace, the stack trace won't be printed
     * with the log message.
     *
     * @return the [Logger.Builder] to build the [Logger]

     */
    fun disableStackTrace(): Logger.Builder {
        return Logger.Builder().disableStackTrace()
    }

    /**
     * Start to customize a [Logger] and enable border, the border would surround the entire log
     * content, and separate the log message, thread info and stack trace.
     *
     * @return the [Logger.Builder] to build the [Logger]
     * @see BorderFormatter

     */
    fun enableBorder(): Logger.Builder {
        return Logger.Builder().enableBorder()
    }

    /**
     * Start to customize a [Logger] and disable border, the log content won't be surrounded
     * by a border.
     *
     * @return the [Logger.Builder] to build the [Logger]

     */
    fun disableBorder(): Logger.Builder {
        return Logger.Builder().disableBorder()
    }

    /**
     * Start to customize a [Logger] and set the [JsonFormatter].
     *
     * @param jsonFormatter the [JsonFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun jsonFormatter(jsonFormatter: JsonFormatter): Logger.Builder {
        return Logger.Builder().jsonFormatter(jsonFormatter)
    }

    /**
     * Start to customize a [Logger] and set the [XmlFormatter].
     *
     * @param xmlFormatter the [XmlFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun xmlFormatter(xmlFormatter: XmlFormatter): Logger.Builder {
        return Logger.Builder().xmlFormatter(xmlFormatter)
    }

    /**
     * Start to customize a [Logger] and set the [ThrowableFormatter].
     *
     * @param throwableFormatter the [ThrowableFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun throwableFormatter(throwableFormatter: ThrowableFormatter): Logger.Builder {
        return Logger.Builder().throwableFormatter(throwableFormatter)
    }

    /**
     * Start to customize a [Logger] and set the [ThreadFormatter].
     *
     * @param threadFormatter the [ThreadFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun threadFormatter(threadFormatter: ThreadFormatter): Logger.Builder {
        return Logger.Builder().threadFormatter(threadFormatter)
    }

    /**
     * Start to customize a [Logger] and set the [StackTraceFormatter].
     *
     * @param stackTraceFormatter the [StackTraceFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun stackTraceFormatter(stackTraceFormatter: StackTraceFormatter): Logger.Builder {
        return Logger.Builder().stackTraceFormatter(stackTraceFormatter)
    }

    /**
     * Start to customize a [Logger] and set the [BorderFormatter].
     *
     * @param borderFormatter the [BorderFormatter] to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun borderFormatter(borderFormatter: BorderFormatter): Logger.Builder {
        return Logger.Builder().borderFormatter(borderFormatter)
    }

    /**
     * Start to customize a [Logger] and add an object formatter for specific class of object.
     *
     * @param objectClass     the class of object
     * @param objectFormatter the object formatter to add
     * @param <T>             the type of object
     * @return the [Logger.Builder] to build the [Logger]

     */
    fun <T> addObjectFormatter(
        objectClass: Class<T>,
        objectFormatter: ObjectFormatter<in T>
    ): Logger.Builder {
        return Logger.Builder().addObjectFormatter(objectClass, objectFormatter)
    }

    /**
     * Start to customize a [Logger] and add an interceptor.
     *
     * @param interceptor the interceptor to add
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun addInterceptor(interceptor: Interceptor): Logger.Builder {
        return Logger.Builder().addInterceptor(interceptor)
    }

    /**
     * Start to customize a [Logger] and set the [Printer] array.
     *
     * @param printers the [Printer] array to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun printers(vararg printers: Printer): Logger.Builder {
        return Logger.Builder().printers(*printers)
    }

    /**
     * Log an object with level [LogLevel.VERBOSE].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun v(obj: Any) {
        assertInitialization()
        sLogger.v(obj)
    }

    /**
     * Log an array with level [LogLevel.VERBOSE].
     *
     * @param array the array to log
     */
    fun v(array: Array<Any>?) {
        assertInitialization()
        sLogger.v(array)
    }

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun v(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.v(format, *args)
    }

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     */
    fun v(msg: String?) {
        assertInitialization()
        sLogger.v(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun v(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.v(msg, tr)
    }

    /**
     * Log an object with level [LogLevel.DEBUG].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun d(obj: Any?) {
        assertInitialization()
        sLogger.d(obj)
    }

    /**
     * Log an array with level [LogLevel.DEBUG].
     *
     * @param array the array to log
     */
    fun d(array: Array<Any>?) {
        assertInitialization()
        sLogger.d(array)
    }

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun d(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.d(format, *args)
    }

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     */
    fun d(msg: String) {
        assertInitialization()
        sLogger.d(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun d(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.d(msg, tr)
    }

    /**
     * Log an object with level [LogLevel.INFO].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun i(obj: Any?) {
        assertInitialization()
        sLogger.i(obj)
    }

    /**
     * Log an array with level [LogLevel.INFO].
     *
     * @param array the array to log
     */
    fun i(array: Array<Any>?) {
        assertInitialization()
        sLogger.i(array)
    }

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun i(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.i(format, *args)
    }

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param msg the message to log
     */
    fun i(msg: String?) {
        assertInitialization()
        sLogger.i(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.INFO].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun i(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.i(msg, tr)
    }

    /**
     * Log an object with level [LogLevel.WARN].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun w(obj: Any?) {
        assertInitialization()
        sLogger.w(obj)
    }

    /**
     * Log an array with level [LogLevel.WARN].
     *
     * @param array the array to log
     */
    fun w(array: Array<Any>?) {
        assertInitialization()
        sLogger.w(array)
    }

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun w(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.w(format, *args)
    }

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param msg the message to log
     */
    fun w(msg: String?) {
        assertInitialization()
        sLogger.w(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.WARN].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun w(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.w(msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ERROR].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun e(obj: Any?) {
        assertInitialization()
        sLogger.e(obj)
    }

    /**
     * Log an array with level [LogLevel.ERROR].
     *
     * @param array the array to log
     */
    fun e(array: Array<Any>?) {
        assertInitialization()
        sLogger.e(array)
    }

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun e(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.e(format, *args)
    }

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     */
    fun e(msg: String?) {
        assertInitialization()
        sLogger.e(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun e(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.e(msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ASSERT].
     *
     * @param obj the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun wtf(obj: Any?) {
        assertInitialization()
        sLogger.e(obj)
    }

    /**
     * Log an array with level [LogLevel.ASSERT].
     *
     * @param array the array to log
     */
    fun wtf(array: Array<Any>?) {
        assertInitialization()
        sLogger.e(array)
    }

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun wtf(format: String, vararg args: Any) {
        assertInitialization()
        sLogger.e(format, *args)
    }

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     */
    fun wtf(msg: String?) {
        assertInitialization()
        sLogger.e(msg)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun wtf(msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.e(msg, tr)
    }


    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param obj   the object to log
     * @see LogConfiguration.Builder.addObjectFormatter

     */
    fun log(logLevel: Int, obj: Any?) {
        assertInitialization()
        sLogger.log(logLevel, obj)
    }

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log

     */
    fun log(logLevel: Int, array: Array<Any>?) {
        assertInitialization()
        sLogger.log(logLevel, array)
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param format   the format of the message to log
     * @param args     the arguments of the message to log

     */
    fun log(logLevel: Int, format: String, vararg args: Any) {
        assertInitialization()
        sLogger.log(logLevel, format, *args)
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log

     */
    fun log(logLevel: Int, msg: String?) {
        assertInitialization()
        sLogger.log(logLevel, msg)
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log

     */
    fun log(logLevel: Int, msg: String?, tr: Throwable) {
        assertInitialization()
        sLogger.log(logLevel, msg, tr)
    }

    /**
     * Log a JSON string, with level [LogLevel.DEBUG] by default.
     *
     * @param json the JSON string to log
     */
    fun json(json: String, logLevel: Int = LogLevel.DEBUG) {
        assertInitialization()
        sLogger.json(json, logLevel)
    }

    /**
     * Log a XML string, with level [LogLevel.DEBUG] by default.
     *
     * @param xml the XML string to log
     */
    fun xml(xml: String, logLevel: Int = LogLevel.DEBUG) {
        assertInitialization()
        sLogger.xml(xml, logLevel)
    }
}
