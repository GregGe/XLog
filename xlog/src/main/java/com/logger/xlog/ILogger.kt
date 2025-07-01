package com.logger.xlog

interface ILogger {
    /**
     * The config of Logger
     */
    val loggerConfig: ILoggerConfig

    /**
     * Enable the dynamic tag
     */
    var dynamicTag: Boolean

    /**
     * Start to customize a [Logger] and set the tag.
     *
     * @param tag the tag to customize
     * @return the [Logger.Builder] to build the [Logger]
     */
    fun tag(tag: String): ILogger

    /**
     * Whether logs with specific level is loggable.
     *
     * @param level the specific level
     * @return true if loggable, false otherwise
     */
    fun isLoggable(level: Int): Boolean

    /**
     * Log an object with level [LogLevel.VERBOSE].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun v(obj: Any?)

    /**
     * Log an array with level [LogLevel.VERBOSE].
     *
     * @param array the array to log
     */
    fun v(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun v(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun v(msg: String?, tr: Throwable)

    /**
     * Log an object with level [LogLevel.DEBUG].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun d(obj: Any?)

    /**
     * Log an array with level [LogLevel.DEBUG].
     *
     * @param array the array to log
     */
    fun d(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun d(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun d(msg: String?, tr: Throwable)

    /**
     * Log an object with level [LogLevel.INFO].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun i(obj: Any?)

    /**
     * Log an array with level [LogLevel.INFO].
     *
     * @param array the array to log
     */
    fun i(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun i(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.INFO].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun i(msg: String?, tr: Throwable)

    /**
     * Log an object with level [LogLevel.WARN].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun w(obj: Any?)

    /**
     * Log an array with level [LogLevel.WARN].
     *
     * @param array the array to log
     */
    fun w(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun w(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.WARN].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun w(msg: String?, tr: Throwable)

    /**
     * Log an object with level [LogLevel.ERROR].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun e(obj: Any?)

    /**
     * Log an array with level [LogLevel.ERROR].
     *
     * @param array the array to log
     */
    fun e(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun e(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun e(msg: String?, tr: Throwable)

    /**
     * Log an object with level [LogLevel.ASSERT].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun wtf(obj: Any?)

    /**
     * Log an array with level [LogLevel.ASSERT].
     *
     * @param array the array to log
     */
    fun wtf(array: Array<Any>)

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    fun wtf(msg: String?, vararg args: Any)

    /**
     * Log a message and a throwable with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    fun wtf(msg: String?, tr: Throwable)

    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param obj   the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    fun log(logLevel: Int, obj: Any?)

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log
     *
     */
    fun log(logLevel: Int, array: Array<Any>)

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param format   the format of the message to log, null if just need to concat arguments
     * @param args     the arguments of the message to log
     *
     */
    fun log(logLevel: Int, format: String?, vararg args: Any)

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     *
     */
    fun log(logLevel: Int, msg: String?, tr: Throwable)

    /**
     * Log a JSON string, with level [LogLevel.DEBUG] by default.
     *
     * @param json the JSON string to log
     */
    fun json(json: String, logLevel: Int = LogLevel.DEBUG)

    /**
     * Log a XML string, with level [LogLevel.DEBUG] by default.
     *
     * @param xml the XML string to log
     */
    fun xml(xml: String, logLevel: Int = LogLevel.DEBUG)

    /**
     * Print an object in a new line.
     *
     * @param logLevel the log level of the printing object
     * @param obj   the object to print
     */
    fun <T> println(logLevel: Int, obj: T?)

    /**
     * Print an array in a new line.
     *
     * @param logLevel the log level of the printing array
     * @param array    the array to print
     */
    fun println(logLevel: Int, array: Array<Any>)

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg   the format of the printing log, null if just need to concat arguments
     * @param args     the arguments of the printing log
     */
    fun println(logLevel: Int, msg: String?, vararg args: Any)

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     * @param tr       a throwable object to log
     */
    fun println(logLevel: Int, msg: String?, tr: Throwable)

    /**
     * Get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    fun getStackTraceString(tr: Throwable): String
}
