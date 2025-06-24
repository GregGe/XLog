package com.logger.xlog.printer.file.backup

/**
 * An abstract [BackupStrategy], simply append '.bak.n' to the end of normal file name when
 * naming a backup with index n.
 *
 *
 * Developers can simply extend this class when defining their own [BackupStrategy].
 *
 * 
 */
abstract class AbstractBackupStrategy : BackupStrategy {
    override fun getBackupFileName(fileName: String, backupIndex: Int): String {
        return "$fileName.bak.$backupIndex"
    }
}
