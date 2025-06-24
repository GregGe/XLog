package com.logger.xlog

import com.logger.xlog.LogLevel.ALL
import com.logger.xlog.LogLevel.DEBUG
import com.logger.xlog.LogLevel.ERROR
import com.logger.xlog.LogLevel.INFO
import com.logger.xlog.LogLevel.NONE
import com.logger.xlog.LogLevel.VERBOSE
import com.logger.xlog.LogLevel.WARN
import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.interceptor.Interceptor
import com.logger.xlog.internal.SystemCompat
import com.logger.xlog.printer.Printer
import com.logger.xlog.utils.AssertUtil
import com.logger.xlog.utils.XLogUtil
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class XLogTest {
    private val logsContainer: MutableList<LogItem> = ArrayList()

    @Before
    fun setup() {
        XLogUtil.beforeTest()
        XLog.init(
            LogConfiguration.Builder().logLevel(ALL).tag(DEFAULT_TAG).build(),
            ContainerPrinter(logsContainer)
        )
    }

    @Test
    fun testSimpleLogging() {
        XLog.i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, MESSAGE)
    }

    @Test
    fun testLogLevel() {
        XLog.i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, MESSAGE)

        // Test WARN
        var logger = XLog.logLevel(WARN).build()
        logsContainer.clear()
        logger.i(MESSAGE)
        AssertUtil.assertNoLog(logsContainer)
        logsContainer.clear()
        logger.w(MESSAGE)
        assertLog(WARN, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.e(MESSAGE)
        assertLog(ERROR, DEFAULT_TAG, MESSAGE)

        // Test NONE
        logger = XLog.logLevel(NONE).build()
        logsContainer.clear()
        logger.log(-1, MESSAGE)
        logger.log(0, MESSAGE)
        logger.log(1, MESSAGE)
        logger.v(MESSAGE)
        logger.d(MESSAGE)
        logger.i(MESSAGE)
        logger.w(MESSAGE)
        logger.e(MESSAGE)
        logger.log(7, MESSAGE)
        logger.log(8, MESSAGE)
        logger.log(9, MESSAGE)
        AssertUtil.assertNoLog(logsContainer)


        // Test ALL
        logger = XLog.logLevel(ALL).build()
        logsContainer.clear()
        logger.log(-1, MESSAGE)
        assertLog(-1, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.log(0, MESSAGE)
        assertLog(0, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.log(1, MESSAGE)
        assertLog(1, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.v(MESSAGE)
        assertLog(VERBOSE, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.d(MESSAGE)
        assertLog(DEBUG, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.w(MESSAGE)
        assertLog(WARN, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.e(MESSAGE)
        assertLog(ERROR, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.log(7, MESSAGE)
        assertLog(7, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.log(8, MESSAGE)
        assertLog(8, DEFAULT_TAG, MESSAGE)
        logsContainer.clear()
        logger.log(9, MESSAGE)
        assertLog(9, DEFAULT_TAG, MESSAGE)
    }

    @Test
    fun testTag() {
        XLog.i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, MESSAGE)

        logsContainer.clear()
        XLog.tag(CUSTOM_TAG).i(MESSAGE)
        assertLog(INFO, CUSTOM_TAG, MESSAGE)
    }

    @Test
    fun testThread() {
        XLog.enableThreadInfo().i("Message with thread info")
        var result = (logsContainer.size == 1
                && logsContainer[0].msg!!.contains("Thread: "))
        assertTrue("No thread info found", result)

        logsContainer.clear()
        XLog.disableThreadInfo().i("Message without thread info")
        result = (logsContainer.size == 1
                && !logsContainer[0].msg!!.contains("Thread: "))
        assertTrue("Thread info found", result)
    }

    @Test
    fun testStackTrace() {
        XLog.enableStackTrace(1).i("Message with stack trace, depth 1")
        var result = (logsContainer.size == 1
                && logsContainer[0].msg!!.contains("\t─ "))
        assertTrue("No stack trace found", result)

        logsContainer.clear()
        XLog.enableStackTrace(2).i("Message with stack trace, depth 2")
        result = (logsContainer.size == 1
                && logsContainer[0].msg!!.contains("\t├ "))
        assertTrue("No stack trace found", result)

        logsContainer.clear()
        XLog.disableStackTrace().i("Message without stack trace")
        result = (logsContainer.size == 1
                && !logsContainer[0].msg!!.contains("\t├ "))
        assertTrue("Stack trace found", result)
    }

    @Test
    fun testBorder() {
        XLog.enableBorder().i("Message with a border")
        var result = (logsContainer.size == 1 &&
                logsContainer[0].msg!!.trimIndent().startsWith("╔═══") &&
                logsContainer[0].msg!!.endsWith("════"))
        assertTrue("No bordered log found", result)

        logsContainer.clear()
        XLog.disableBorder().i("Message without a border")
        result = (logsContainer.size == 1 && !logsContainer[0].msg!!.startsWith("╔═══")
                && !logsContainer[0].msg!!.endsWith("════"))
        assertTrue("Bordered log found", result)
    }

    @Test
    fun testObject() {
        val date = Date()
        XLog.addObjectFormatter(Date::class.java, object : ObjectFormatter<Date> {
            override fun format(data: Date): String {
                return data.time.toString()
            }
        }).i(date)
        val result = (logsContainer.size == 1
                && logsContainer[0].msg == date.time.toString())
        assertTrue("Formatted object log not found", result)
    }

    @Test
    fun testModifyingInterceptor() {
        XLog.addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.tag = CUSTOM_TAG
                return log
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.msg = log.msg + "[i1]"
                return log
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.msg = log.msg + "[i2]"
                return log
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.level = DEBUG
                return log
            }
        }).i(MESSAGE)
        assertLog(DEBUG, CUSTOM_TAG, MESSAGE + "[i1][i2]")
    }

    @Test
    fun testReplacingInterceptor() {
        XLog.addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem {
                return LogItem(VERBOSE, "tag1", "msg1")
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem {
                return LogItem(DEBUG, "tag2", "msg2")
            }
        }).i(MESSAGE)
        assertLog(DEBUG, "tag2", "msg2")
    }

    @Test
    fun testBlockingInterceptor() {
        XLog.addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.msg = "i1"
                return log
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                // Block the log.
                return null
            }
        }).addInterceptor(object : Interceptor {
            override fun intercept(log: LogItem): LogItem? {
                log.msg = "i2"
                return log
            }
        }).i(MESSAGE)
        AssertUtil.assertNoLog(logsContainer)
    }

    @Test
    fun testCustomJsonFormatter() {
        XLog.jsonFormatter(
            object : JsonFormatter {
                override fun format(data: String?): String {
                    return "This is a json string: $data"
                }
            })
            .json("{name=xlog}")
        assertLog(DEBUG, DEFAULT_TAG, "This is a json string: {name=xlog}")
    }

    @Test
    fun testCustomXmlFormatter() {
        XLog.xmlFormatter(
            object : XmlFormatter {
                override fun format(data: String?): String {
                    return "This is a xml string: $data"
                }
            })
            .xml("<note name=\"xlog\">")
        assertLog(DEBUG, DEFAULT_TAG, "This is a xml string: <note name=\"xlog\">")
    }

    @Test
    fun testCustomThrowableFormatter() {
        val formattedThrowable = "This is a throwable"
        XLog.throwableFormatter(
            object : ThrowableFormatter {
                override fun format(data: Throwable?): String {
                    return formattedThrowable
                }
            })
            .i(MESSAGE, Throwable())
        assertLog(
            INFO, DEFAULT_TAG, """
     $MESSAGE
     $formattedThrowable
     """.trimIndent()
        )
    }

    @Test
    fun testCustomThreadFormatter() {
        val formattedThread = "This is the thread info"
        XLog.threadFormatter(
            object : ThreadFormatter {
                override fun format(data: Thread?): String {
                    return formattedThread
                }
            })
            .enableThreadInfo()
            .i(MESSAGE)
        assertLog(
            INFO, DEFAULT_TAG, """
     $formattedThread
     $MESSAGE
     """.trimIndent()
        )
    }

    @Test
    fun testCustomStackTraceFormatter() {
        val formattedStackTrace = "This is the stack trace"
        XLog.stackTraceFormatter(
            object : StackTraceFormatter {
                override fun format(data: Array<StackTraceElement?>?): String {
                    return formattedStackTrace
                }
            })
            .enableStackTrace(1)
            .i(MESSAGE)
        assertLog(
            INFO, DEFAULT_TAG, """
     $formattedStackTrace
     $MESSAGE
     """.trimIndent()
        )
    }

    @Test
    fun testCustomBorderFormatter() {
        XLog.enableThreadInfo().threadFormatter(object : ThreadFormatter {
            override fun format(data: Thread?): String {
                return "T1"
            }
        }).enableBorder().borderFormatter(object : BorderFormatter {
            override fun format(segments: Array<String?>?): String {
                return addCustomBorder(segments)
            }
        }).i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, addCustomBorder(arrayOf("T1", MESSAGE)))
    }

    private fun addCustomBorder(segments: Array<String?>?): String {
        if (segments.isNullOrEmpty()) {
            return ""
        }

        val nonNullSegments = arrayOfNulls<String>(segments.size)
        var nonNullCount = 0
        for (segment in segments) {
            if (segment != null) {
                nonNullSegments[nonNullCount++] = segment
            }
        }
        if (nonNullCount == 0) {
            return ""
        }
        val msgBuilder = StringBuilder()
        msgBuilder.append("<<").append(SystemCompat.lineSeparator)
        for (i in 0 until nonNullCount) {
            msgBuilder.append(nonNullSegments[i])
            if (i != nonNullCount - 1) {
                msgBuilder.append(SystemCompat.lineSeparator).append("--")
                    .append(SystemCompat.lineSeparator)
            } else {
                msgBuilder.append(SystemCompat.lineSeparator).append(">>")
            }
        }
        return msgBuilder.toString()
    }

    @Test
    fun testCustomPrinter() {
        XLog.printers(
            object : ContainerPrinter(logsContainer) {
                override fun onPrint(logItem: LogItem): LogItem {
                    logItem.msg = CUSTOM_PRINTER_MSG_PREFIX + logItem.msg
                    return logItem
                }
            })
            .i(MESSAGE)
        assertLog(INFO, DEFAULT_TAG, CUSTOM_PRINTER_MSG_PREFIX + MESSAGE)
    }

    private fun assertLog(logLevel: Int, tag: String, msg: String) {
        AssertUtil.assertHasLog(logsContainer, LogItem(logLevel, tag, msg))
    }

    companion object {
        private const val MESSAGE = "message"

        private const val DEFAULT_TAG = "XLOG"

        private const val CUSTOM_TAG = "custom_tag"

        private const val CUSTOM_PRINTER_MSG_PREFIX = "message from custom printer - "
    }

    open class ContainerPrinter(logsContainer: MutableList<LogItem>) :
        Printer {
        private var logsContainers: MutableList<LogItem> = ArrayList()

        init {
            this.logsContainers = logsContainer
        }

        override fun println(logLevel: Int, tag: String, msg: String) {
            val log = onPrint(LogItem(logLevel, tag, msg))
            afterPrint(log)
        }

        protected open fun onPrint(logItem: LogItem): LogItem {
            return logItem
        }

        private fun afterPrint(log: LogItem) {
            logsContainers.add(log)
        }
    }

}