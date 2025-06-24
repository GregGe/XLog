package com.logger.xlog.internal.printer.file.backup

import com.logger.xlog.printer.file.backup.BackupStrategy
import java.io.File

object BackupUtil {
    /**
     * Shift existed backups if needed, and backup the logging file.
     *
     * @param loggingFile    the logging file
     * @param backupStrategy the strategy should be use when backing up
     */
    @JvmStatic
    fun backup(loggingFile: File, backupStrategy: BackupStrategy) {
        val loggingFileName = loggingFile.name
        val path = loggingFile.parent
        var backupFile: File
        var nextBackupFile: File
        val maxBackupIndex = backupStrategy.maxBackupIndex
        if (maxBackupIndex > 0) {
            backupFile =
                File(path, backupStrategy.getBackupFileName(loggingFileName, maxBackupIndex))
            if (backupFile.exists()) {
                backupFile.delete()
            }
            for (i in maxBackupIndex - 1 downTo 1) {
                backupFile = File(path, backupStrategy.getBackupFileName(loggingFileName, i))
                if (backupFile.exists()) {
                    nextBackupFile =
                        File(path, backupStrategy.getBackupFileName(loggingFileName, i + 1))
                    backupFile.renameTo(nextBackupFile)
                }
            }
            nextBackupFile = File(path, backupStrategy.getBackupFileName(loggingFileName, 1))
            loggingFile.renameTo(nextBackupFile)
        } else if (maxBackupIndex == BackupStrategy.NO_LIMIT) {
            for (i in 1 until Int.MAX_VALUE) {
                nextBackupFile = File(path, backupStrategy.getBackupFileName(loggingFileName, i))
                if (!nextBackupFile.exists()) {
                    loggingFile.renameTo(nextBackupFile)
                    break
                }
            }
        } else {
            // Illegal maxBackIndex, could not come here.
        }
    }

    /**
     * Check if a [BackupStrategy] is valid, will throw a exception if invalid.
     *
     * @param backupStrategy the backup strategy to be verify
     */
    @JvmStatic
    fun verifyBackupStrategy(backupStrategy: BackupStrategy) {
        val maxBackupIndex = backupStrategy.maxBackupIndex
        require(maxBackupIndex >= 0) { "Max backup index should not be less than 0" }
        require(maxBackupIndex != Int.MAX_VALUE) { "Max backup index too big: $maxBackupIndex" }
    }
}
