package com.logger.xlog.extensions

import com.logger.xlog.ILogger
import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel
import com.logger.xlog.LogLevel.ALL
import com.logger.xlog.LogLevel.DEBUG
import com.logger.xlog.LogLevel.INFO
import com.logger.xlog.Logger
import com.logger.xlog.LoggerConfig
import com.logger.xlog.LoggerParams.Companion.DEFAULT_TAG
import com.logger.xlog.XLog
import com.logger.xlog.XLogTest
import com.logger.xlog.utils.AssertUtil
import com.logger.xlog.utils.XLogUtil
import org.junit.Before
import org.junit.Test

class LoggerExtTest {
    private val logsContainer: MutableList<LogItem> = ArrayList()
    private val containerPrinter = XLogTest.ContainerPrinter(logsContainer)
    private val loggerConfig = LoggerConfig().apply {
        setLogLevel(ALL).setTag(DEFAULT_TAG).printers(containerPrinter)
    }

    private val logger = Logger(loggerConfig)

    @Before
    fun setup() {
        XLogUtil.beforeTest()
        XLog.init(
            LoggerConfig().setLogLevel(ALL).setTag(DEFAULT_TAG),
            containerPrinter
        )
    }

    @Test
    fun newConfigTest() {
        fun newTest(logger: ILogger) {
            val newLogger = logger.newConfig {
                setTag(TAG)
                setLogLevel(LOG_LEVEL)
            }
            newLogger.loggerConfig.loggerParams.apply {
                assert(tag == TAG)
                assert(logLevel == LOG_LEVEL)
                assert(printer != containerPrinter)
            }
        }
        newTest(XLog)
        newTest(logger)
    }

    @Test
    fun cloneConfigTest() {
        fun cloneTest(logger: ILogger) {
            val newLogger = logger.cloneConfig {
                setTag(TAG)
                setLogLevel(LOG_LEVEL)
            }
            newLogger.loggerConfig.loggerParams.apply {
                assert(tag == TAG)
                assert(logLevel == LOG_LEVEL)
                assert(printer == containerPrinter)
            }
        }

        cloneTest(XLog)
        cloneTest(logger)
    }

    @Test
    fun updateConfigTest() {
        fun updateTest(logger: ILogger) {
            val newLogger = logger.updateConfig {
                setTag(TAG)
                setLogLevel(LOG_LEVEL)
            }
            newLogger.loggerConfig.loggerParams.apply {
                assert(logger === newLogger)
                assert(tag == TAG)
                assert(logLevel == LOG_LEVEL)
                assert(printer == containerPrinter)
            }
        }

        updateTest(XLog)
        updateTest(logger)
    }

    @Test
    fun dynamicTagTest() {
        fun tagTest(logger: ILogger) {
            logsContainer.clear()
            logger.d(MESSAGE)
            assertLog(DEBUG, DEFAULT_TAG, MESSAGE)

            logsContainer.clear()
            logger.dynamicTag {
                i(MESSAGE)
            }
            assertLog(INFO, this::class.java.simpleName, MESSAGE)

            logsContainer.clear()
            logger.d(MESSAGE)
            assertLog(DEBUG, DEFAULT_TAG, MESSAGE)
        }

        tagTest(XLog)
        tagTest(logger)
    }

    private fun assertLog(logLevel: Int, tag: String, msg: String) {
        AssertUtil.assertHasLog(logsContainer, LogItem(logLevel, tag, msg))
    }

    companion object {
        const val TAG = "LoggerExtTest"
        const val LOG_LEVEL = LogLevel.ERROR
        const val MESSAGE = "MESSAGE OF LoggerExtTest"
    }
}