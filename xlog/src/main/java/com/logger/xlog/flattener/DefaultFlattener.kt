package com.logger.xlog.flattener

import com.logger.xlog.LogLevel

/**
 * Simply join the timestamp, log level, tag and message together.
 *
 * 
 */
open class DefaultFlattener : Flattener {

    override fun flatten(
        timeMillis: Long,
        logLevel: Int,
        tag: String,
        message: String
    ): CharSequence {
        return "$timeMillis|${LogLevel.getShortLevelName(logLevel)}|$tag|$message"
    }
}
