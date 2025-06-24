package com.logger.xlog.utils

import com.logger.xlog.LogItem
import org.junit.Assert.assertTrue

object AssertUtil {
    fun assertHasLog(logsContainer: List<LogItem>, target: LogItem) {
        var result = false
        for (log in logsContainer) {
            if ((log.level == target.level)
                && log.tag == target.tag
                && log.msg == target.msg
            ) {
                result = true
            }
        }
        assertTrue("Missing log: $target", result)
    }

    fun assertHasLog(logsContainer: List<LogItem>, msg: String) {
        var result = false
        for (log in logsContainer) {
            if (log.msg == msg) {
                result = true
            }
        }
        assertTrue("Missing log: $msg", result)
    }

    fun assertHasLog(logsContainer: List<LogItem?>, position: Int, msg: String) {
        var result = false
        if (logsContainer.size > position && logsContainer[position] != null && logsContainer[position]!!.msg == msg) {
            result = true
        }
        assertTrue("Missing log: $msg", result)
    }

    fun assertNoLog(logsContainer: List<LogItem>) {
        val result = logsContainer.isEmpty()
        assertTrue("Unexpected log found", result)
    }
}
