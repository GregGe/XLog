package com.logger.xlog.printer

/**
 * Represents a group of Printers that should used to print logs in the same time, each printer
 * may probably print the log to different place.
 */
class PrinterSet(vararg printers: Printer) : Printer {
    private val printerSet: MutableSet<Printer> = mutableSetOf()

    /**
     * Constructor, pass printers in and will use all these printers to print the same logs.
     *
     * @param printers the printers used to print the same logs
     */
    init {
        printerSet.addAll(printers)
    }

    override fun println(logLevel: Int, tag: String, msg: String) {
        for (printer in printerSet) {
            printer.println(logLevel, tag, msg)
        }
    }
}
