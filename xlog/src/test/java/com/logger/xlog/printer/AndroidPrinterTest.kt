package com.logger.xlog.printer

import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel
import com.logger.xlog.XLog
import com.logger.xlog.utils.AssertUtil
import com.logger.xlog.utils.RandomUtil
import com.logger.xlog.utils.XLogUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.math.min

class AndroidPrinterTest {
    var logContainer: MutableList<LogItem> = ArrayList()

    @Before
    fun setup() {
        XLogUtil.beforeTest()
        XLog.init(LogLevel.ALL, object : AndroidPrinter() {
            override fun printChunk(logLevel: Int, tag: String?, msg: String) {
                logContainer.add(LogItem(logLevel, tag, msg))
            }
        })
    }

    @Test
    @Throws(Exception::class)
    fun testPrintShortMessage() {
        val msg = "This is a short message"
        XLog.d(msg)
        assertEquals(1, logContainer.size)
        AssertUtil.assertHasLog(logContainer, msg)
    }

    @Test
    @Throws(Exception::class)
    fun testPrint4kMessage() {
        val length = AndroidPrinter.DEFAULT_MAX_CHUNK_SIZE
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(RandomUtil.randomAsciiChar())
        }
        val msg = sb.toString()
        XLog.d(msg)
        assertEquals(1, logContainer.size)
        AssertUtil.assertHasLog(logContainer, msg)
    }

    @Test
    @Throws(Exception::class)
    fun testPrintLongMessage() {
        val messageChunkLength = AndroidPrinter.DEFAULT_MAX_CHUNK_SIZE
        val length = (3.6 * messageChunkLength).toInt()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(RandomUtil.randomAsciiChar())
        }
        val msg = sb.toString()
        XLog.d(msg)
        assertEquals(4, logContainer.size)

        var start = 0
        var end = 0
        var i = 0
        while (end < length) {
            end = AndroidPrinter.adjustEnd(
                msg, start,
                min((start + messageChunkLength).toDouble(), length.toDouble()).toInt()
            )
            val chunk = msg.substring(start, end)
            AssertUtil.assertHasLog(logContainer, i, chunk)

            start = end
            i++
        }
    }
}