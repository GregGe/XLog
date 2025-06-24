package com.logger.xlog.printer.file.backup

import com.logger.xlog.printer.file.backup.BackupStrategy.Companion.NO_LIMIT
import java.io.File

/**
 * Limit the file size of a max length.
 *
 * 
 */
/**
 * Constructor.
 *
 * @param maxSize        the max size the file can reach
 * @param maxBackupIndex the max backup index, or [.NO_LIMIT], see [.getMaxBackupIndex]
 */
class FileSizeBackupStrategy(
    private val maxSize: Long,
    override val maxBackupIndex: Int = NO_LIMIT
) : AbstractBackupStrategy() {
    override fun shouldBackup(file: File): Boolean {
        return file.length() > maxSize
    }
}
