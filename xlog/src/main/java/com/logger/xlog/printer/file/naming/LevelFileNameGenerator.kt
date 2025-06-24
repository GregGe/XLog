package com.logger.xlog.printer.file.naming

import com.logger.xlog.LogLevel

/**
 * Generate file name according to the log level, different levels lead to different file names.
 */
class LevelFileNameGenerator : FileNameGenerator {
    override val isFileNameChangeable: Boolean
        get() = true

    /**
     * Generate a file name which represent a specific log level.
     */
    override fun generateFileName(logLevel: Int, timestamp: Long): String {
        return LogLevel.getLevelName(logLevel)
    }
}
