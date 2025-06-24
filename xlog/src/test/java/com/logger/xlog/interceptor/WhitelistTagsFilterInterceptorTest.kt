package com.logger.xlog.interceptor

import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel
import com.logger.xlog.interceptor.WhitelistTagsFilterInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WhitelistTagsFilterInterceptorTest {
    private var interceptor: WhitelistTagsFilterInterceptor? = null

    @Before
    fun setup() {
        interceptor = WhitelistTagsFilterInterceptor("abc", "def")
    }

    @Test
    @Throws(Exception::class)
    fun testWhitelist() {
        assertTagAccepted("abc")
        assertTagAccepted("def")

        assertTagRejected("")
        assertTagRejected("ab")
        assertTagRejected("abcd")
        assertTagRejected("bcd")
        assertTagRejected("abcdef")
        assertTagRejected("defg")
        assertTagRejected("ef")
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