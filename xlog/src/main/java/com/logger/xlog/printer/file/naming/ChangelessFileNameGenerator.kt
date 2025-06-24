package com.logger.xlog.printer.file.naming

/**
 * Generate a file name that is changeless.
 */
class ChangelessFileNameGenerator
/**
 * Constructor.
 *
 * @param fileName the changeless file name
 */(private val fileName: String) : FileNameGenerator {
    override val isFileNameChangeable: Boolean
        get() = false

    override fun generateFileName(logLevel: Int, timestamp: Long): String {
        return fileName
    }
}
