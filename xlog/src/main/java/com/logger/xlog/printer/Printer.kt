package com.logger.xlog.printer

import com.logger.xlog.printer.file.FilePrinter

/**
 * A printer is used for printing the log to somewhere, like android shell, terminal
 * or file system.
 *
 *
 * There are 4 main implementation of Printer.
 * <br></br>[AndroidPrinter], print log to android shell terminal.
 * <br></br>[ConsolePrinter], print log to console via System.out.
 * <br></br>[FilePrinter], print log to file system.
 * <br></br>[RemotePrinter], print log to remote server, this is empty implementation yet.
 */
interface Printer {
    /**
     * Print log in new line.
     *
     * @param logLevel the level of log
     * @param tag      the tag of log
     * @param msg      the msg of log
     */
    fun println(logLevel: Int, tag: String, msg: String)
}
