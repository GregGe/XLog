package com.logger.xlog.printer.file

import com.logger.xlog.flattener.Flattener
import com.logger.xlog.internal.DefaultsFactory.createBackupStrategy
import com.logger.xlog.internal.DefaultsFactory.createCleanStrategy
import com.logger.xlog.internal.DefaultsFactory.createFileNameGenerator
import com.logger.xlog.internal.DefaultsFactory.createFlattener
import com.logger.xlog.internal.DefaultsFactory.createWriter
import com.logger.xlog.internal.Platform.Companion.get
import com.logger.xlog.internal.printer.file.backup.BackupUtil.backup
import com.logger.xlog.internal.printer.file.backup.BackupUtil.verifyBackupStrategy
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.file.FilePrinter.Builder
import com.logger.xlog.printer.file.backup.BackupStrategy
import com.logger.xlog.printer.file.clean.CleanStrategy
import com.logger.xlog.printer.file.naming.FileNameGenerator
import com.logger.xlog.printer.file.writer.Writer
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.Volatile

/**
 * Log [Printer] using file system. When print a log, it will print it to the specified file.
 *
 *
 * Use the [Builder] to construct a [FilePrinter] object.
 */
class FilePrinter internal constructor(builder: Builder) :
    Printer {
    /**
     * The folder path of log file.
     */
    private val folderPath = builder.folderPath

    /**
     * The file name generator for log file.
     */
    private val fileNameGenerator: FileNameGenerator?

    /**
     * The backup strategy for log file.
     */
    private val backupStrategy: BackupStrategy?

    /**
     * The clean strategy for log file.
     */
    private val cleanStrategy: CleanStrategy?

    /**
     * The flattener when print a log.
     */
    private val flattener: Flattener?

    /**
     * Log writer.
     */
    private val writer: Writer?

    @Volatile
    private lateinit var worker: Worker

    /*package*/
    init {
        fileNameGenerator = builder.fileNameGenerator
        backupStrategy = builder.backupStrategy
        cleanStrategy = builder.cleanStrategy
        flattener = builder.flattener
        writer = builder.writer

        if (USE_WORKER) {
            worker = Worker()
        }

        checkLogFolder()
    }

    /**
     * Make sure the folder of log file exists.
     */
    private fun checkLogFolder() {
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    override fun println(logLevel: Int, tag: String, msg: String) {
        val timeMillis = System.currentTimeMillis()
        if (USE_WORKER) {
            if (!worker.isStarted()) {
                worker.start()
            }
            worker.enqueue(LogItem(timeMillis, logLevel, tag, msg))
        } else {
            doPrintln(timeMillis, logLevel, tag, msg)
        }
    }

    /**
     * Do the real job of writing log to file.
     */
    private fun doPrintln(timeMillis: Long, logLevel: Int, tag: String, msg: String) {
        if (writer == null) {
            return
        }
        var lastFileName = writer.openedFileName
        val isWriterClosed = !writer.isOpened
        if (lastFileName == null || isWriterClosed || fileNameGenerator!!.isFileNameChangeable) {
            val newFileName =
                fileNameGenerator?.generateFileName(logLevel, System.currentTimeMillis())
            if (newFileName == null || newFileName.trim { it <= ' ' }.isEmpty()) {
                get().error("File name should not be empty, ignore log: $msg")
                return
            }
            if (newFileName != lastFileName || isWriterClosed) {
                writer.close()
                cleanLogFilesIfNecessary()
                if (!writer.open(File(folderPath, newFileName))) {
                    return
                }
                lastFileName = newFileName
            }
        }

        val lastFile = writer.openedFile
        if (backupStrategy!!.shouldBackup(lastFile!!)) {
            // Backup the log file, and create a new log file.
            writer.close()
            backup(lastFile, backupStrategy)
            if (!writer.open(File(folderPath, lastFileName))) {
                return
            }
        }
        val flattenedLog = flattener!!.flatten(timeMillis, logLevel, tag, msg).toString()
        writer.appendLog(flattenedLog)
    }

    /**
     * Clean log files if should clean follow strategy
     */
    private fun cleanLogFilesIfNecessary() {
        val logDir = File(folderPath)
        val files = logDir.listFiles() ?: return
        for (file in files) {
            if (cleanStrategy!!.shouldClean(file)) {
                file.delete()
            }
        }
    }

    /**
     * Builder for [FilePrinter].
     */
    class Builder
    /**
     * Construct a builder.
     *
     * @param folderPath the folder path of log file
     */(
        /**
         * The folder path of log file.
         */
        var folderPath: String
    ) {
        /**
         * The file name generator for log file.
         */
        var fileNameGenerator: FileNameGenerator? = null

        /**
         * The backup strategy for log file.
         */
        var backupStrategy: BackupStrategy? = null

        /**
         * The clean strategy for log file.
         */
        var cleanStrategy: CleanStrategy? = null

        /**
         * The flattener when print a log.
         */
        var flattener: Flattener? = null

        /**
         * The writer to write log into log file.
         */
        var writer: Writer? = null

        /**
         * Set the file name generator for log file.
         *
         * @param fileNameGenerator the file name generator for log file
         * @return the builder
         */
        fun fileNameGenerator(fileNameGenerator: FileNameGenerator?): Builder {
            this.fileNameGenerator = fileNameGenerator
            return this
        }

        /**
         * Set the backup strategy for log file.
         *
         * @param backupStrategy the backup strategy for log file
         * @return the builder
         */
        fun backupStrategy(backupStrategy: BackupStrategy): Builder {
            this.backupStrategy = backupStrategy

            verifyBackupStrategy(backupStrategy)
            return this
        }

        /**
         * Set the clean strategy for log file.
         *
         * @param cleanStrategy the clean strategy for log file
         * @return the builder
         * 
         */
        fun cleanStrategy(cleanStrategy: CleanStrategy?): Builder {
            this.cleanStrategy = cleanStrategy
            return this
        }

        /**
         * Set the flattener when print a log.
         *
         * @param flattener the flattener when print a log
         * @return the builder
         * 
         */
        fun flattener(flattener: Flattener?): Builder {
            this.flattener = flattener
            return this
        }

        /**
         * Set the writer to write log into log file.
         *
         * @param writer the writer to write log into log file
         * @return the builder
         * 
         */
        fun writer(writer: Writer?): Builder {
            this.writer = writer
            return this
        }

        /**
         * Build configured [FilePrinter] object.
         *
         * @return the built configured [FilePrinter] object
         */
        fun build(): FilePrinter {
            fillEmptyFields()
            return FilePrinter(this)
        }

        private fun fillEmptyFields() {
            if (fileNameGenerator == null) {
                fileNameGenerator = createFileNameGenerator()
            }
            if (backupStrategy == null) {
                backupStrategy = createBackupStrategy()
            }
            if (cleanStrategy == null) {
                cleanStrategy = createCleanStrategy()
            }
            if (flattener == null) {
                flattener = createFlattener()
            }
            if (writer == null) {
                writer = createWriter()
            }
        }
    }

    private class LogItem(var timeMillis: Long, var level: Int, var tag: String, var msg: String)

    /**
     * Work in background, we can enqueue the logs, and the worker will dispatch them.
     */
    private inner class Worker : Runnable {
        private val logs: BlockingQueue<LogItem> = LinkedBlockingQueue()

        @Volatile
        private var started = false

        /**
         * Enqueue the log.
         *
         * @param log the log to be written to file
         */
        fun enqueue(log: LogItem) {
            try {
                logs.put(log)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        /**
         * Whether the worker is started.
         *
         * @return true if started, false otherwise
         */
        fun isStarted(): Boolean {
            synchronized(this) {
                return started
            }
        }

        /**
         * Start the worker.
         */
        fun start() {
            synchronized(this) {
                if (started) {
                    return
                }
                Thread(this).start()
                started = true
            }
        }

        override fun run() {
            var log: LogItem
            try {
                while ((logs.take().also { log = it }) != null) {
                    doPrintln(log.timeMillis, log.level, log.tag, log.msg)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                synchronized(this) {
                    started = false
                }
            }
        }
    }

    companion object {
        /**
         * Use worker, write logs asynchronously.
         */
        private const val USE_WORKER = true
    }
}
