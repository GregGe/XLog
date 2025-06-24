package com.logger.xlog.printer.file.writer

import java.io.File

/**
 * A writer is used to write log into log file.
 *
 *
 * Used in worker thread.
 *
 * 
 */
abstract class Writer {
    /**
     * Open a specific log file for future writing, if it doesn't exist yet, just create it.
     *
     * @param file the specific log file, may not exist
     * @return true if the log file is successfully opened, false otherwise
     */
    abstract fun open(file: File): Boolean

    /**
     * Whether a log file is successfully opened in previous [.open].
     *
     * @return true if log file is opened, false otherwise
     */
    abstract val isOpened: Boolean

    /**
     * Get the opened log file.
     *
     * @return the opened log file, or null if log file not opened
     */
    abstract val openedFile: File?

    /**
     * Get the name of opened log file.
     *
     * @return the name of opened log file, or null if log file not opened
     */
    abstract val openedFileName: String?

    /**
     * Append the log to the end of the opened log file, normally an extra line separator is needed.
     *
     * @param log the log to append
     */
    abstract fun appendLog(log: String?)

    /**
     * Make sure the opened log file is closed, normally called before switching the log file.
     *
     * @return true if the log file is successfully closed, false otherwise
     */
    abstract fun close(): Boolean
}
