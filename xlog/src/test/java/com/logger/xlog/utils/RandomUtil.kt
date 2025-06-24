package com.logger.xlog.utils

import java.util.Random

object RandomUtil {
    private val sAsciiCharRandom = Random()

    fun randomAsciiChar(): Char {
        return (sAsciiCharRandom.nextInt(100) + 28 /* Just don't random to a line separator*/).toChar()
    }
}
