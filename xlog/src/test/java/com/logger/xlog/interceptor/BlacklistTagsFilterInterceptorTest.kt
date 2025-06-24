package com.logger.xlog.interceptor

import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel
import com.logger.xlog.interceptor.BlacklistTagsFilterInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BlacklistTagsFilterInterceptorTest {
    private var interceptor: BlacklistTagsFilterInterceptor? = null

    @Before
    fun setup() {
        interceptor = BlacklistTagsFilterInterceptor("abc", "def")
    }

    @Test
    @Throws(Exception::class)
    fun testBlacklist() {
        assertTagRejected("abc")
        assertTagRejected("def")

        assertTagAccepted("")
        assertTagAccepted("ab")
        assertTagAccepted("abcd")
        assertTagAccepted("bcd")
        assertTagAccepted("abcdef")
        assertTagAccepted("defg")
        assertTagAccepted("ef")
    }

    private fun assertTagAccepted(tag: String) {
        val log = LogItem(LogLevel.DEBUG, tag, "Message")
        assertNotNull("Tag " + log.tag + " should be accepted", interceptor!!.intercept(log))
    }

    private fun assertTagRejected(tag: String) {
        val log = LogItem(LogLevel.DEBUG, tag, "Message")
        assertNull("Tag " + log.tag + " should be rejected", interceptor!!.intercept(log))
    }
}