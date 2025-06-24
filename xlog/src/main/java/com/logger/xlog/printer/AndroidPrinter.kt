package com.logger.xlog.printer

import android.util.Log
import com.logger.xlog.LogLevel
import kotlin.math.min

/**
 * Log [Printer] using [android.util.Log].
 */
open class AndroidPrinter
/**
 * Constructor.
 *
 * @param autoSeparate whether the message should be separated by line separator automatically.
 * Imaging there is a message "line1\nline2\nline3", and each line has chars
 * less than max-chunk-size, then the message would be separated to 3 lines
 * automatically
 * @param maxChunkSize the max size of each chunk. If the message is too long, it will be
 * separated to several chunks automatically
 */ @JvmOverloads constructor(
    /**
     * Whether the log should be separated by line separator automatically.
     */
    private val autoSeparate: Boolean = false,
    private val maxChunkSize: Int = DEFAULT_MAX_CHUNK_SIZE
) : Printer {
    override fun println(logLevel: Int, tag: String, msg: String) {
        val msgLength = msg.length
        var start = 0
        var end: Int
        while (start < msgLength) {
            if (msg[start] == '\n') {
                start++
                continue
            }
            end = min((start + maxChunkSize).toDouble(), msgLength.toDouble()).toInt()
            if (autoSeparate) {
                val newLine = msg.indexOf('\n', start)
                end = if (newLine != -1) min(end.toDouble(), newLine.toDouble()).toInt() else end
            } else {
                end = adjustEnd(msg, start, end)
            }
            printChunk(logLevel, tag, msg.substring(start, end))

            start = end
        }
    }

    /**
     * Print single chunk of log in new line.
     *
     * @param logLevel the level of log
     * @param tag      the tag of log
     * @param msg      the msg of log
     */
    open fun printChunk(logLevel: Int, tag: String?, msg: String) {
        if (logLevel == LogLevel.ASSERT) {
            Log.wtf(tag, msg)
        } else {
            Log.println(logLevel, tag, msg)
        }
    }

    companion object {
        /**
         * Generally, android has a default length limit of 4096 for single log, but
         * some device(like HUAWEI) has its own shorter limit, so we just use 4000
         * and wish it could run well in all devices.
         */
        const val DEFAULT_MAX_CHUNK_SIZE: Int = 4000

        /**
         * Move the end to the nearest line separator('\n') (if exist).
         */
        @JvmStatic
        fun adjustEnd(msg: String, start: Int, originEnd: Int): Int {
            if (originEnd == msg.length) {
                // Already end of message.
                return originEnd
            }
            if (msg[originEnd] == '\n') {
                // Already prior to '\n'.
                return originEnd
            }
            // Search back for '\n'.
            var last = originEnd - 1
            while (start < last) {
                if (msg[last] == '\n') {
                    return last
                }
                last--
            }
            return originEnd
        }
    }
}
