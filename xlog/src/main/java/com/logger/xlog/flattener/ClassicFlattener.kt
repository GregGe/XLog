package com.logger.xlog.flattener

import com.logger.xlog.LogLevel

/**
 * The classic flattener, flatten the log with pattern "{@value #DEFAULT_PATTERN}".
 * <p>
 * Imagine there is a log, with [LogLevel.DEBUG] level, "my_tag" tag and "Simple message"
 * message, the flattened log would be: "2016-11-30 13:00:00.000 D/my_tag: Simple message"
 *
 * 
 */
class ClassicFlattener : PatternFlattener(DEFAULT_PATTERN) {

    companion object {
        // 2016-11-30 13:00:00.000 D/my_tag: Simple message
        private const val DEFAULT_PATTERN = "{d} {l}/{t}: {m}"
    }
}
