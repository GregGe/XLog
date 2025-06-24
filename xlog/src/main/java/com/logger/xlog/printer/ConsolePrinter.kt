package com.logger.xlog.printer

import android.os.SystemClock
import com.logger.xlog.flattener.Flattener
import com.logger.xlog.internal.DefaultsFactory.createFlattener

/**
 * Log [Printer] using `System.out.println(String)`.
 *
 * 
 */
open class ConsolePrinter(private var flattener: Flattener = createFlattener()) : Printer {
    override fun println(logLevel: Int, tag: String, msg: String) {
        val flattenedLog =
            flattener.flatten(SystemClock.uptimeMillis(), logLevel, tag, msg).toString()
        println(flattenedLog)
    }
}
