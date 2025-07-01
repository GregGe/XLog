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
import com.logger.xlog.internal.DefaultsFactory.builtinObjectFormatters
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.PrinterSet
import kotlin.collections.set

/**
 * The configuration used for logging, always attached to a [Logger], will affect all logs
 * logged by the [Logger].
 *
 */
class LoggerConfig internal constructor(override var loggerParams: LoggerParams = LoggerParams()) :
    ILoggerConfig {
    init {
        initEmptyFieldsWithDefaultValues()
    }

    override fun setLogLevel(logLevel: Int): LoggerConfig {
        loggerParams.logLevel = logLevel
        return this
    }

    override fun setTag(tag: String): LoggerConfig {
        loggerParams.tag = tag
        return this
    }

    override fun enableThreadInfo(): LoggerConfig {
        loggerParams.withThread = true
        return this
    }


    override fun disableThreadInfo(): LoggerConfig {
        loggerParams.withThread = false
        return this
    }

    override fun enableStackTrace(depth: Int, stackTraceOrigin: String?): LoggerConfig {
        loggerParams.withStackTrace = true
        loggerParams.stackTraceOrigin = stackTraceOrigin
        loggerParams.stackTraceDepth = depth
        return this
    }

    override fun disableStackTrace(): LoggerConfig {
        loggerParams.withStackTrace = false
        loggerParams.stackTraceOrigin = null
        loggerParams.stackTraceDepth = 0
        return this
    }

    override fun enableBorder(): LoggerConfig {
        loggerParams.withBorder = true
        return this
    }

    override fun disableBorder(): LoggerConfig {
        loggerParams.withBorder = false
        return this
    }

    override fun jsonFormatter(jsonFormatter: JsonFormatter?): LoggerConfig {
        loggerParams.jsonFormatter = jsonFormatter
        return this
    }

    override fun xmlFormatter(xmlFormatter: XmlFormatter?): LoggerConfig {
        loggerParams.xmlFormatter = xmlFormatter
        return this
    }

    override fun throwableFormatter(throwableFormatter: ThrowableFormatter?): LoggerConfig {
        loggerParams.throwableFormatter = throwableFormatter
        return this
    }

    override fun threadFormatter(threadFormatter: ThreadFormatter?): LoggerConfig {
        loggerParams.threadFormatter = threadFormatter
        return this
    }

    override fun stackTraceFormatter(stackTraceFormatter: StackTraceFormatter?): LoggerConfig {
        loggerParams.stackTraceFormatter = stackTraceFormatter
        return this
    }

    override fun borderFormatter(borderFormatter: BorderFormatter?): LoggerConfig {
        loggerParams.borderFormatter = borderFormatter
        return this
    }

    override fun <R> addObjectFormatter(
        objectClass: Class<R>, objectFormatter: ObjectFormatter<in R>
    ): LoggerConfig {
        if (loggerParams.objectFormatters == null) {
            loggerParams.objectFormatters = builtinObjectFormatters()
        }
        loggerParams.objectFormatters!![objectClass] = objectFormatter
        return this
    }

    override fun objectFormatters(objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>>): LoggerConfig {
        loggerParams.objectFormatters = objectFormatters
        return this
    }

    override fun addInterceptor(interceptor: Interceptor): LoggerConfig {
        if (loggerParams.interceptors == null) {
            loggerParams.interceptors = mutableListOf()
        }
        loggerParams.interceptors!!.add(interceptor)
        return this
    }

    override fun interceptors(interceptors: MutableList<Interceptor>?): LoggerConfig {
        loggerParams.interceptors = interceptors
        return this
    }

    override fun printers(vararg printers: Printer): LoggerConfig {
        if (printers.isEmpty()) {
            // Is there anybody want to reuse the Builder? It's not a good idea, but
            // anyway, in case you want to reuse a builder and do not want the custom
            // printers anymore, just do it.
            loggerParams.printer = null
        } else if (printers.size == 1) {
            loggerParams.printer = printers[0]
        } else {
            loggerParams.printer = PrinterSet(*printers)
        }
        return this
    }

    override fun initEmptyFieldsWithDefaultValues() {
        loggerParams.apply {
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
            if (printer == null) {
                printer = DefaultsFactory.createPrinter()
            }
        }
    }
}
