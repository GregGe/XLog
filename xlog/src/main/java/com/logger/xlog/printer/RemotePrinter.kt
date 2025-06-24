package com.logger.xlog.printer

/**
 * Log [Printer] which should print the log to remote server.
 *
 *
 * This is just a empty implementation telling you that you can do
 * such thing, you can override [.println] )} and sending the log by your
 * implementation.
 */
class RemotePrinter : Printer {
    override fun println(logLevel: Int, tag: String, msg: String) {
        // TODO: Send the log to your server.
    }
}
