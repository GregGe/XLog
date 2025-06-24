package com.logger.xlog.printer.file.backup

import java.io.File

/**
 * Never backup the log file.
 *
 * 
 */
class NeverBackupStrategy(override val maxBackupIndex: Int = 1) : BackupStrategy {
    override fun shouldBackup(file: File): Boolean {
        return false
    }

    override fun getBackupFileName(fileName: String, backupIndex: Int): String {
        return ""
    }
}
