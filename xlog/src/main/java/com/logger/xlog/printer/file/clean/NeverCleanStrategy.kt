package com.logger.xlog.printer.file.clean

import java.io.File

/**
 * Never Limit the file life.
 *
 * 
 */
class NeverCleanStrategy : CleanStrategy {
    override fun shouldClean(file: File): Boolean {
        return false
    }
}
