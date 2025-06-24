package com.logger.xlog.printer.file.backup

import com.logger.xlog.internal.printer.file.backup.BackupUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class BackupTest {
    var logPath: String = "build/test/log"

    var logFileName: String = "log"

    @Before
    @Throws(IOException::class)
    fun setup() {
        // Clean log folder.
        val folder = File(logPath)
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }

        // Ensure folder exist.
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndex1() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = 1

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndex2() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = 2

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndex5() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = 5

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(3, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(5, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(5, backupStrategy)
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndex5WithMissingFile() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = 5

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(3, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        val file2 = File(logPath, backupStrategy.getBackupFileName(logFileName, 2))
        file2.delete()

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(4)
        assertFileExists(backupStrategy, 1)
        assertFileExists(backupStrategy, 2)
        assertFileExists(backupStrategy, 4)
        assertFileExists(backupStrategy, 5)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(4)
        assertFileExists(backupStrategy, 1)
        assertFileExists(backupStrategy, 2)
        assertFileExists(backupStrategy, 3)
        assertFileExists(backupStrategy, 5)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(4)
        assertFileExists(backupStrategy, 1)
        assertFileExists(backupStrategy, 2)
        assertFileExists(backupStrategy, 3)
        assertFileExists(backupStrategy, 4)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(5)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(5)
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndexNoLimit() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = BackupStrategy.NO_LIMIT

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(3, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(5, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(6, backupStrategy)
    }

    @Test
    @Throws(Exception::class)
    fun testBackupMaxIndexNoLimitWithMissingFile() {
        val backupStrategy: BackupStrategy = object : AbstractBackupStrategy() {
            override val maxBackupIndex: Int
                get() = BackupStrategy.NO_LIMIT

            override fun shouldBackup(file: File): Boolean {
                return true
            }
        }

        val logFile = File(logPath, logFileName)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(1, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(2, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(3, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        val file2 = File(logPath, backupStrategy.getBackupFileName(logFileName, 2))
        file2.delete()

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        val file1 = File(logPath, backupStrategy.getBackupFileName(logFileName, 1))
        file1.delete()
        val file3 = File(logPath, backupStrategy.getBackupFileName(logFileName, 3))
        file3.delete()

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(3)
        assertFileExists(backupStrategy, 1)
        assertFileExists(backupStrategy, 2)
        assertFileExists(backupStrategy, 4)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFiles(4, backupStrategy)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(5)

        logFile.createNewFile()
        BackupUtil.backup(logFile, backupStrategy)
        assertFilesCount(6)
    }

    private fun assertFiles(fileCount: Int, backupStrategy: BackupStrategy) {
        assertFilesCount(fileCount)
        for (i in 1..fileCount) {
            assertFileExists(backupStrategy, i)
        }
    }

    private fun assertFilesCount(filesCount: Int) {
        val folder = File(logPath)
        val files = checkNotNull(folder.listFiles())
        assertEquals(filesCount, files.size)
    }

    private fun assertFileExists(backupStrategy: BackupStrategy, index: Int) {
        val file = File(logPath, backupStrategy.getBackupFileName(logFileName, index))
        assert(file.exists())
    }
}