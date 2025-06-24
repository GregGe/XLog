package com.logger.xlog.printer.file.clean

import java.io.File

/**
 * Limit the file life of a max time.
 *
 * 
 */
class FileLastModifiedCleanStrategy
/**
 * Constructor.
 *
 * @param maxTimeMillis the max time the file can keep
 */(private val maxTimeMillis: Long) : CleanStrategy {
    override fun shouldClean(file: File): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val lastModified = file.lastModified()
        return (currentTimeMillis - lastModified > maxTimeMillis)
    }
}
