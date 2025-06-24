package com.logger.xlog.formatter

/**
 * Thrown to indicate that the format of the data is something wrong.
 */
class FormatException(cause: Throwable?) : RuntimeException(cause) {

    companion object {
        private const val serialVersionUID = -5365630128856068164L
    }
}
