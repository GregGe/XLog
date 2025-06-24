package com.logger.xlog.internal

/**
 * System environment.
 */
object SystemCompat {
    /**
     * The line separator of system.
     */
    @JvmField
    var lineSeparator: String = Platform.get().lineSeparator()
}
