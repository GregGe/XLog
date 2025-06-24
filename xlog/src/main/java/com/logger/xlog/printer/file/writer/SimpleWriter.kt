package com.logger.xlog.printer.file.writer

import com.logger.xlog.internal.Platform.Companion.get
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * A simple implementation of [Writer].
 *
 *
 * Subclass can override [.onNewFileCreated] and do some initialization work to the new
 * file, such as calling [.appendLog] to add a file header.
 *
 * 
 */
open class SimpleWriter : Writer() {
    /**
     * The name of opened log file.
     */
    final override var openedFileName: String? = null
        private set

    /**
     * The opened log file.
     */
    final override var openedFile: File? = null
        private set

    private var bufferedWriter: BufferedWriter? = null

    override fun open(file: File): Boolean {
        openedFileName = file.name
        openedFile = file

        var isNewFile = false

        // Create log file if not exists.
        if (!openedFile!!.exists()) {
            try {
                val parent = openedFile!!.parentFile
                if (!parent!!.exists()) {
                    parent.mkdirs()
                }
                openedFile!!.createNewFile()
                isNewFile = true
            } catch (e: Exception) {
                e.printStackTrace()
                close()
                return false
            }
        }

        // Create buffered writer.
        try {
            bufferedWriter = BufferedWriter(FileWriter(openedFile, true))
            if (isNewFile) {
                onNewFileCreated(openedFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            close()
            return false
        }
        return true
    }

    override val isOpened: Boolean
        get() = bufferedWriter != null && openedFile!!.exists()

    /**
     * Called after a log file is newly created.
     *
     *
     * You can do some initialization work to the new file, such as calling [.appendLog]
     * to add a file header.
     *
     *
     * Called in worker thread.
     *
     * @param file the newly created log file
     */
    open fun onNewFileCreated(file: File?) {
    }

    override fun appendLog(log: String?) {
        try {
            bufferedWriter!!.write(log)
            bufferedWriter!!.newLine()
            bufferedWriter!!.flush()
        } catch (e: Exception) {
            get().warn("append log failed: " + e.message)
        }
    }

    override fun close(): Boolean {
        if (bufferedWriter != null) {
            try {
                bufferedWriter!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        bufferedWriter = null
        openedFileName = null
        openedFile = null
        return true
    }
}
