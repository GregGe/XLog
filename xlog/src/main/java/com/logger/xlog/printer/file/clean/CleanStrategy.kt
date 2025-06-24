package com.logger.xlog.printer.file.clean

import java.io.File

/**
 * Decide whether the log file should be clean.
 *
 * 
 */
interface CleanStrategy {
    /**
     * Whether we should clean a specified log file.
     *
     * @param file the log file
     * @return true is we should clean the log file
     */
    fun shouldClean(file: File): Boolean
}
