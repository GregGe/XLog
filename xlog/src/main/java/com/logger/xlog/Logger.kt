package com.logger.xlog

import android.os.Build
import com.logger.xlog.Logger.Builder
import com.logger.xlog.extensions.safePlus
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.internal.Platform.Companion.get
import com.logger.xlog.internal.util.StackTraceUtil
import java.util.regex.Pattern

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
class Logger internal constructor(override val loggerConfig: ILoggerConfig) :
    ILoggerConfig by loggerConfig, ILogger {
    override var dynamicTag: Boolean = false

    @get:JvmSynthetic
    internal val explicitTag = ThreadLocal<String>()

    /**
     * 获取特定对象的 [ObjectFormatter]。
     *
     * @param obj 要格式化的对象
     * @param <T> 对象的类型
     * @return 匹配的对象格式化器，如果没有找到则返回 null
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> getObjectFormatter(obj: T): ObjectFormatter<in T>? {
        if (loggerParams.objectFormatters.isNullOrEmpty()) {
            return null
        }

        var clazz: Class<in T>
        var superClazz: Class<in T>? = obj!!::class.java as Class<in T>
        var formatter: ObjectFormatter<in T>?
        do {
            clazz = superClazz!!
            formatter = loggerParams.objectFormatters!![clazz] as ObjectFormatter<in T>?
            superClazz = clazz.superclass
        } while (formatter == null && superClazz != null)
        return formatter
    }

    /**
     * Whether logs with specific level is loggable.
     *
     * @param level the specific level
     * @return true if loggable, false otherwise
     */
    override fun isLoggable(level: Int): Boolean {
        return level >= loggerParams.logLevel
    }

    override fun tag(tag: String): Logger {
        explicitTag.set(tag)
        return this
    }

    private fun getCurrentTag(): String {
        val customTag = explicitTag.get()
        if (customTag != null) {
            explicitTag.remove()
            return customTag
        }

        if (dynamicTag) {
            return getDynamicTag() ?: loggerParams.tag
        }

        return loggerParams.tag
    }

    private fun clipTag(tag: String): String {
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= 26) {
            // 若未超过限制或系统版本足够高，直接返回标签
            tag
        } else {
            // 若超过限制，截取前 MAX_TAG_LENGTH 个字符作为标签
            tag.substring(0, MAX_TAG_LENGTH)
        }
    }

    /**
     * Log an object with level [LogLevel.VERBOSE].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun v(obj: Any?) {
        println(LogLevel.VERBOSE, obj)
    }

    /**
     * Log an array with level [LogLevel.VERBOSE].
     *
     * @param array the array to log
     */
    override fun v(array: Array<Any>) {
        println(LogLevel.VERBOSE, array)
    }

    /**
     * Log a message with level [LogLevel.VERBOSE].
     *
     * @param msg the format of the message to log
     * @param args   the arguments of the message to log
     */
    override fun v(msg: String?, vararg args: Any) {
        println(LogLevel.VERBOSE, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.VERBOSE].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun v(msg: String?, tr: Throwable) {
        println(LogLevel.VERBOSE, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.DEBUG].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun d(obj: Any?) {
        println(LogLevel.DEBUG, obj)
    }

    /**
     * Log an array with level [LogLevel.DEBUG].
     *
     * @param array the array to log
     */
    override fun d(array: Array<Any>) {
        println(LogLevel.DEBUG, array)
    }

    /**
     * Log a message with level [LogLevel.DEBUG].
     *
     * @param msg the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    override fun d(msg: String?, vararg args: Any) {
        println(LogLevel.DEBUG, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.DEBUG].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun d(msg: String?, tr: Throwable) {
        println(LogLevel.DEBUG, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.INFO].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun i(obj: Any?) {
        println(LogLevel.INFO, obj)
    }

    /**
     * Log an array with level [LogLevel.INFO].
     *
     * @param array the array to log
     */
    override fun i(array: Array<Any>) {
        println(LogLevel.INFO, array)
    }

    /**
     * Log a message with level [LogLevel.INFO].
     *
     * @param msg the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    override fun i(msg: String?, vararg args: Any) {
        println(LogLevel.INFO, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.INFO].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun i(msg: String?, tr: Throwable) {
        println(LogLevel.INFO, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.WARN].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun w(obj: Any?) {
        println(LogLevel.WARN, obj)
    }

    /**
     * Log an array with level [LogLevel.WARN].
     *
     * @param array the array to log
     */
    override fun w(array: Array<Any>) {
        println(LogLevel.WARN, array)
    }

    /**
     * Log a message with level [LogLevel.WARN].
     *
     * @param msg the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    override fun w(msg: String?, vararg args: Any) {
        println(LogLevel.WARN, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.WARN].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun w(msg: String?, tr: Throwable) {
        println(LogLevel.WARN, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ERROR].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun e(obj: Any?) {
        println(LogLevel.ERROR, obj)
    }

    /**
     * Log an array with level [LogLevel.ERROR].
     *
     * @param array the array to log
     */
    override fun e(array: Array<Any>) {
        println(LogLevel.ERROR, array)
    }

    /**
     * Log a message with level [LogLevel.ERROR].
     *
     * @param msg the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    override fun e(msg: String?, vararg args: Any) {
        println(LogLevel.ERROR, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ERROR].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun e(msg: String?, tr: Throwable) {
        println(LogLevel.ERROR, msg, tr)
    }

    /**
     * Log an object with level [LogLevel.ASSERT].
     *
     * @param obj the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun wtf(obj: Any?) {
        println(LogLevel.ASSERT, obj)
    }

    /**
     * Log an array with level [LogLevel.ASSERT].
     *
     * @param array the array to log
     */
    override fun wtf(array: Array<Any>) {
        println(LogLevel.ASSERT, array)
    }

    /**
     * Log a message with level [LogLevel.ASSERT].
     *
     * @param msg the format of the message to log, null if just need to concat arguments
     * @param args   the arguments of the message to log
     */
    override fun wtf(msg: String?, vararg args: Any) {
        println(LogLevel.ASSERT, msg, *args)
    }

    /**
     * Log a message and a throwable with level [LogLevel.ASSERT].
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    override fun wtf(msg: String?, tr: Throwable) {
        println(LogLevel.ASSERT, msg, tr)
    }

    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param obj   the object to log
     * @see ILoggerConfig.addObjectFormatter
     *
     */
    override fun log(logLevel: Int, obj: Any?) {
        println(logLevel, obj)
    }

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log
     *
     */
    override fun log(logLevel: Int, array: Array<Any>) {
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
    override fun log(logLevel: Int, format: String?, vararg args: Any) {
        println(logLevel, format, *args)
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     *
     */
    override fun log(logLevel: Int, msg: String?, tr: Throwable) {
        println(logLevel, msg, tr)
    }

    /**
     * Log a JSON string, with level [LogLevel.DEBUG] by default.
     *
     * @param json the JSON string to log
     */
    override fun json(json: String, logLevel: Int) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        printlnInternal(logLevel, loggerParams.jsonFormatter!!.format(json))
    }

    /**
     * Log a XML string, with level [LogLevel.DEBUG] by default.
     *
     * @param xml the XML string to log
     */
    override fun xml(xml: String, logLevel: Int) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        printlnInternal(logLevel, loggerParams.xmlFormatter!!.format(xml))
    }

    /**
     * Print an object in a new line.
     *
     * @param logLevel the log level of the printing object
     * @param obj   the object to print
     */
    override fun <T> println(logLevel: Int, obj: T?) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        val objectString: String
        if (obj != null) {
            val objectFormatter: ObjectFormatter<in T>? = getObjectFormatter(obj)
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
    override fun println(logLevel: Int, array: Array<Any>) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        printlnInternal(logLevel, array.contentDeepToString())
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg   the format of the printing log, null if just need to concat arguments
     * @param args     the arguments of the printing log
     */
    override fun println(logLevel: Int, msg: String?, vararg args: Any) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        printlnInternal(logLevel, formatArgs(msg, *args))
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     * @param tr       a throwable object to log
     */
    override fun println(logLevel: Int, msg: String?, tr: Throwable) {
        if (logLevel < loggerParams.logLevel) {
            return
        }
        printlnInternal(logLevel, msg.safePlus() + loggerParams.throwableFormatter!!.format(tr))
    }

    override fun getStackTraceString(tr: Throwable): String {
        return StackTraceUtil.getStackTraceString(tr)
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
        var tag = clipTag(getCurrentTag())
        var thread = if (loggerParams.withThread) {
            loggerParams.threadFormatter!!.format(Thread.currentThread())
        } else {
            null
        }
        var stackTrace = if (loggerParams.withStackTrace) {
            loggerParams.stackTraceFormatter!!.format(
                StackTraceUtil.getCroppedRealStackTrack(
                    Throwable().stackTrace,
                    loggerParams.stackTraceOrigin,
                    loggerParams.stackTraceDepth
                )
            )
        } else {
            null
        }

        if (loggerParams.interceptors != null) {
            var log = LogItem(logLvl, tag, thread, stackTrace, msg)
            for (interceptor in loggerParams.interceptors!!) {
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

        val finalMessage = if (loggerParams.withBorder) {
            loggerParams.borderFormatter!!.format(arrayOf(thread, stackTrace, msg)) ?: ""
        } else {
            "${thread.safePlus()}${stackTrace.safePlus()}$msg"
        }

        loggerParams.printer!!.println(logLvl, tag, finalMessage)
    }

    /**
     * Format a string with arguments.
     *
     * @param message the format string, null if just to concat the arguments
     * @param args   the arguments
     * @return the formatted string
     */
    private fun formatArgs(message: String?, vararg args: Any): String {
        if (message != null) {
            return if (args.isEmpty()) {
                message
            } else {
                String.format(message, *args)
            }
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

    private val fqcnIgnore = setOf(
        XLog::class.java.name,
        Logger::class.java.name,
        Builder::class.java.name,
        ILogger::class.java.name,
    )

    private fun getDynamicTag(): String? {
        return Throwable().stackTrace
            .first { it.className !in fqcnIgnore }
            .let(::createStackElementTag)
    }

    /**
     * Extract the tag which should be used for the message from the `element`. By default
     * this will use the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     *
     * Note: This will not be called if a [manual tag][.tag] was specified.
     */
    // 从堆栈跟踪元素中提取标签
    fun createStackElementTag(element: StackTraceElement): String? {
        // 获取类名的最后一部分作为标签
        var tag = element.className.substringAfterLast('.')
        // 使用正则表达式匹配匿名类后缀
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            // 若匹配到匿名类后缀，移除该后缀
            tag = m.replaceAll("")
        }
        // Tag length limit was removed in API 26.
        // 判断标签长度是否超过限制或系统版本是否高于 API 26
        return tag
    }

    /**
     * Builder for [Logger].
     */
    class Builder(private val loggerConfig: LoggerConfig = LoggerConfig()) :
        ILoggerConfig by loggerConfig {
        fun build(): Logger {
            return Logger(loggerConfig)
        }
    }

    // 伴生对象，定义常量和静态成员
    companion object {
        // 定义日志消息的最大长度
        private const val MAX_LOG_LENGTH = 4000

        // 定义标签的最大长度
        private const val MAX_TAG_LENGTH = 23

        // 定义用于匹配匿名类后缀的正则表达式模式
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }
}
