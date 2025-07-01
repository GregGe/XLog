package com.logger.xlog

import com.logger.xlog.internal.DefaultsFactory
import com.logger.xlog.printer.Printer

/**
 * A log tool which can be used in android or java, the most important feature is it can print the
 * logs to multiple place in the same time, such as android shell, console and file, you can
 * even print the log to the remote server if you want, all of these can be done just within one
 * calling.
 * <br>Also, XLog is very flexible, almost every component is replaceable.
 */

private val logger: ILogger = Logger.Builder().build()

object XLog : ILogger by logger, ILoggerConfig by logger.loggerConfig {
    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     */
    fun init(logLevel: Int) {
        init(LoggerConfig().setLogLevel(logLevel))
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     */
    fun init(tag: String, logLevel: Int) {
        init(LoggerConfig().setTag(tag).setLogLevel(logLevel))
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfig the log configuration
     */
    fun init(logConfig: LoggerConfig) {
        init(logConfig, DefaultsFactory.createPrinter())
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param printers the printers, each log would be printed by all of the printers
     */
    fun init(vararg printers: Printer) {
        init(LoggerConfig().printers(*printers))
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     * @param printers the printers, each log would be printed by all of the printers
     */
    fun init(logLevel: Int, vararg printers: Printer) {
        init(LoggerConfig().setLogLevel(logLevel).printers(*printers))
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfig the log configuration
     * @param printers         the printers, each log would be printed by all of the printers
     */
    fun init(logConfig: LoggerConfig, vararg printers: Printer) {
        logConfig.printers(*printers)
        updateLoggerConfig(logConfig)
    }

    fun createNewConfig(): LoggerConfig {
        return LoggerConfig()
    }

    fun createNewLogger(): Logger {
        return Logger.Builder(createNewConfig()).build()
    }

    fun cloneNewLogger(): Logger {
        val config = LoggerConfig()
        config.updateLoggerConfig(loggerConfig)
        return Logger.Builder(config).build()
    }
}
