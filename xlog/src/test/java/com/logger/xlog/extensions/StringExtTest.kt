package com.logger.xlog.extensions

import com.logger.xlog.internal.SystemCompat
import org.junit.Test

class StringExtTest {
    @Test
    fun safePlusTest() {
        val nullString: String? = null
        assert(nullString.safePlus() == "")
        val message = "A long String"
        assert(message.safePlus() == message.plus(SystemCompat.lineSeparator))
        assert(message.safePlus("\t") == message.plus("\t"))
    }
}