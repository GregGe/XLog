package com.logger.xlog.extensions

import com.logger.xlog.ILogger
import com.logger.xlog.ILoggerConfig
import com.logger.xlog.Logger
import com.logger.xlog.LoggerConfig

fun ILogger.updateConfig(block: ILoggerConfig.() -> ILoggerConfig): ILogger {
    block(this.loggerConfig)
    return this
}

fun ILogger.cloneConfig(block: ILoggerConfig.() -> ILoggerConfig): ILogger {
    val config = LoggerConfig().updateLoggerConfig(loggerConfig)
    block(config)
    return Logger(config)
}

fun ILogger.newConfig(block: ILoggerConfig.() -> ILoggerConfig): ILogger {
    val newConfig = LoggerConfig()
    newConfig.setTag(loggerConfig.loggerParams.tag)
    newConfig.setLogLevel(loggerConfig.loggerParams.logLevel)
    block(newConfig)
    return Logger(newConfig)
}

fun ILogger.dynamicTag(block: ILogger.() -> Unit) {
    val default = dynamicTag
    dynamicTag = true
    block(this)
    dynamicTag = default
}