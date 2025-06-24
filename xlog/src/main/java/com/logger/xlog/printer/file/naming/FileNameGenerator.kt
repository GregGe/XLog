package com.logger.xlog.printer.file.naming

/**
 * Generates names for log files.
 */
interface FileNameGenerator {
    /**
     * Whether the generated file name will change or not.
     *
     * @return true if the file name is changeable
     */
    val isFileNameChangeable: Boolean

    /**
     * Generate file name for specified log level and timestamp.
     *
     * @param logLevel  the level of the log
     * @param timestamp the timestamp when the logging happen
     * @return the generated file name
     */
    fun generateFileName(logLevel: Int, timestamp: Long): String
}
