package com.logger.xlog

import com.logger.xlog.LogConfiguration.Builder
import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.interceptor.Interceptor
import com.logger.xlog.internal.DefaultsFactory
import com.logger.xlog.internal.DefaultsFactory.builtinObjectFormatters
import kotlin.collections.set

/**
 * The configuration used for logging, always attached to a [Logger], will affect all logs
 * logged by the [Logger].
 *
 *
 * Use the [Builder] to construct a [LogConfiguration] object.
 */
class LogConfiguration internal constructor(builder: Builder) {
    /**
     * The log level, the logs below of which would not be printed.
     */
    val logLevel: Int

    @get:JvmSynthetic // Hide from public API.
    internal val explicitTag = ThreadLocal<String>()

    /**
     * The tag string.
     */
    val tag: String
        get() {
            val customTag = explicitTag.get()
            if (customTag != null) {
                explicitTag.remove()
                return customTag
            }
            return field
        }

    /**
     * Whether we should log with thread info.
     */
    val withThread: Boolean

    /**
     * Whether we should log with stack trace.
     */
    val withStackTrace: Boolean

    /**
     * The origin of stack trace elements from which we should not log when logging with stack trace,
     * it can be a package name like "com.logger.xlog", a class name like "com.yourdomain.logWrapper",
     * or something else between package name and class name, like "com.yourdomain.".
     *
     *
     * It is mostly used when you are using a logger wrapper.
     *
     *
     */
    val stackTraceOrigin: String?

    /**
     * The number of stack trace elements we should log when logging with stack trace,
     * 0 if no limitation.
     */
    val stackTraceDepth: Int

    /**
     * Whether we should log with border.
     */
    val withBorder: Boolean

    /**
     * The JSON formatter used to format the JSON string when log a JSON string.
     */
    val jsonFormatter: JsonFormatter?

    /**
     * The XML formatter used to format the XML string when log a XML string.
     */
    val xmlFormatter: XmlFormatter?

    /**
     * The throwable formatter used to format the throwable when log a message with throwable.
     */
    val throwableFormatter: ThrowableFormatter?

    /**
     * The thread formatter used to format the thread when logging.
     */
    val threadFormatter: ThreadFormatter?

    /**
     * The stack trace formatter used to format the stack trace when logging.
     */
    val stackTraceFormatter: StackTraceFormatter?

    /**
     * The border formatter used to format the border when logging.
     */
    val borderFormatter: BorderFormatter?

    /**
     * The object formatters, used when logging an object.
     */
    private val objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>?

    /**
     * The interceptors, used to intercept the log when logging.
     *
     *
     */
    val interceptors: List<Interceptor>?

    /*package*/
    init {
        logLevel = builder.logLevel

        tag = builder.tag

        withThread = builder.withThread
        withStackTrace = builder.withStackTrace
        stackTraceOrigin = builder.stackTraceOrigin
        stackTraceDepth = builder.stackTraceDepth
        withBorder = builder.withBorder

        jsonFormatter = builder.jsonFormatter
        xmlFormatter = builder.xmlFormatter
        throwableFormatter = builder.throwableFormatter
        threadFormatter = builder.threadFormatter
        stackTraceFormatter = builder.stackTraceFormatter
        borderFormatter = builder.borderFormatter

        objectFormatters = builder.objectFormatters

        interceptors = builder.interceptors
    }

    /**
     * 获取特定对象的 [ObjectFormatter]。
     *
     * @param obj 要格式化的对象
     * @param <T> 对象的类型
     * @return 匹配的对象格式化器，如果没有找到则返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getObjectFormatter(obj: T): ObjectFormatter<in T>? {
        if (objectFormatters == null) {
            return null
        }

        var clazz: Class<in T>
        var superClazz: Class<in T>? = obj!!::class.java as Class<in T>
        var formatter: ObjectFormatter<in T>?
        do {
            clazz = superClazz!!
            formatter = objectFormatters[clazz] as ObjectFormatter<in T>?
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
    fun isLoggable(level: Int): Boolean {
        return level >= logLevel
    }

    /**
     * Builder for [LogConfiguration].
     */
    class Builder {
        /**
         * The log level, the logs below of which would not be printed.
         */
        var logLevel: Int = DEFAULT_LOG_LEVEL

        /**
         * The tag string used when log.
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
         * Construct a builder with all default configurations.
         */
        constructor()

        /**
         * Construct a builder with all configurations from another [LogConfiguration].
         *
         * @param logConfiguration the [LogConfiguration] to copy configurations from
         */
        constructor(logConfiguration: LogConfiguration) {
            logLevel = logConfiguration.logLevel

            tag = logConfiguration.tag

            withThread = logConfiguration.withThread
            withStackTrace = logConfiguration.withStackTrace
            stackTraceOrigin = logConfiguration.stackTraceOrigin
            stackTraceDepth = logConfiguration.stackTraceDepth
            withBorder = logConfiguration.withBorder

            jsonFormatter = logConfiguration.jsonFormatter
            xmlFormatter = logConfiguration.xmlFormatter
            throwableFormatter = logConfiguration.throwableFormatter
            threadFormatter = logConfiguration.threadFormatter
            stackTraceFormatter = logConfiguration.stackTraceFormatter
            borderFormatter = logConfiguration.borderFormatter

            if (logConfiguration.objectFormatters != null) {
                objectFormatters = logConfiguration.objectFormatters
            }

            if (logConfiguration.interceptors != null) {
                interceptors = ArrayList(logConfiguration.interceptors)
            }
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
         * Set the tag string used when log.
         *
         * @param tag the tag string used when log
         * @return the builder
         */
        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        /**
         * Enable thread info, the thread info would be printed with the log message.
         *
         * @return the builder
         * @see ThreadFormatter
         */
        fun enableThreadInfo(): Builder {
            this.withThread = true
            return this
        }

        /**
         * Disable thread info, the thread info won't be printed with the log message.
         *
         * @return the builder
         */
        fun disableThreadInfo(): Builder {
            this.withThread = false
            return this
        }

        /**
         * Enable stack trace, the stack trace would be printed with the log message.
         *
         * @param depth the number of stack trace elements we should log, 0 if no limitation
         * @return the builder
         * @see StackTraceFormatter
         *
         */
        fun enableStackTrace(depth: Int): Builder {
            enableStackTrace(null, depth)
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
         * @param depth the number of stack trace elements we should log, 0 if no limitation
         * @return the builder
         * @see StackTraceFormatter
         *
         */
        fun enableStackTrace(stackTraceOrigin: String?, depth: Int): Builder {
            this.withStackTrace = true
            this.stackTraceOrigin = stackTraceOrigin
            this.stackTraceDepth = depth
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
            return this
        }

        /**
         * Disable border, the log content won't be surrounded by a border.
         *
         * @return the builder
         */
        fun disableBorder(): Builder {
            this.withBorder = false
            return this
        }

        /**
         * Set the JSON formatter used when log a JSON string.
         *
         * @param jsonFormatter the JSON formatter used when log a JSON string
         * @return the builder
         */
        fun jsonFormatter(jsonFormatter: JsonFormatter?): Builder {
            this.jsonFormatter = jsonFormatter
            return this
        }

        /**
         * Set the XML formatter used when log a XML string.
         *
         * @param xmlFormatter the XML formatter used when log a XML string
         * @return the builder
         */
        fun xmlFormatter(xmlFormatter: XmlFormatter?): Builder {
            this.xmlFormatter = xmlFormatter
            return this
        }

        /**
         * Set the throwable formatter used when log a message with throwable.
         *
         * @param throwableFormatter the throwable formatter used when log a message with throwable
         * @return the builder
         */
        fun throwableFormatter(throwableFormatter: ThrowableFormatter?): Builder {
            this.throwableFormatter = throwableFormatter
            return this
        }

        /**
         * Set the thread formatter used when logging.
         *
         * @param threadFormatter the thread formatter used when logging
         * @return the builder
         */
        fun threadFormatter(threadFormatter: ThreadFormatter?): Builder {
            this.threadFormatter = threadFormatter
            return this
        }

        /**
         * Set the stack trace formatter used when logging.
         *
         * @param stackTraceFormatter the stack trace formatter used when logging
         * @return the builder
         */
        fun stackTraceFormatter(stackTraceFormatter: StackTraceFormatter?): Builder {
            this.stackTraceFormatter = stackTraceFormatter
            return this
        }

        /**
         * Set the border formatter used when logging.
         *
         * @param borderFormatter the border formatter used when logging
         * @return the builder
         */
        fun borderFormatter(borderFormatter: BorderFormatter?): Builder {
            this.borderFormatter = borderFormatter
            return this
        }

        /**
         * Add a [ObjectFormatter] for specific class of object.
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
         * Copy all object formatters, only for internal usage.
         *
         * @param objectFormatters the object formatters to copy
         * @return the builder
         */
        fun objectFormatters(objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>): Builder {
            this.objectFormatters = objectFormatters
            return this
        }

        /**
         * Add an interceptor.
         *
         * @param interceptor the interceptor to add
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
         * Copy all interceptors, only for internal usage.
         *
         * @param interceptors the interceptors to copy
         * @return the builder
         */
        fun interceptors(interceptors: MutableList<Interceptor>?): Builder {
            this.interceptors = interceptors
            return this
        }

        /**
         * Builds configured [LogConfiguration] object.
         *
         * @return the built configured [LogConfiguration] object
         */
        fun build(): LogConfiguration {
            initEmptyFieldsWithDefaultValues()
            return LogConfiguration(this)
        }

        private fun initEmptyFieldsWithDefaultValues() {
            if (jsonFormatter == null) {
                jsonFormatter = DefaultsFactory.createJsonFormatter()
            }
            if (xmlFormatter == null) {
                xmlFormatter = DefaultsFactory.createXmlFormatter()
            }
            if (throwableFormatter == null) {
                throwableFormatter = DefaultsFactory.createThrowableFormatter()
            }
            if (threadFormatter == null) {
                threadFormatter = DefaultsFactory.createThreadFormatter()
            }
            if (stackTraceFormatter == null) {
                stackTraceFormatter = DefaultsFactory.createStackTraceFormatter()
            }
            if (borderFormatter == null) {
                borderFormatter = DefaultsFactory.createBorderFormatter()
            }
            if (objectFormatters == null) {
                objectFormatters = builtinObjectFormatters()
            }
        }

        companion object {
            private const val DEFAULT_LOG_LEVEL = LogLevel.ALL

            private const val DEFAULT_TAG = "XLog"
        }
    }
}
