package com.logger.xlog

import com.logger.xlog.XLog.assertInitialization
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Utilities for convenience.
 *
 * 
 */
object LogUtils {
    private const val BUFFER_SIZE = 8 * 1024 // 8K
    /**
     * Format a JSON string using default JSON formatter.
     *
     * @param json the JSON string to format
     * @return the formatted string
     */
    fun formatJson(json: String): String? {
        assertInitialization()
        return XLog.sLogConfiguration.jsonFormatter!!.format(json)
    }

    /**
     * Format an XML string using default XML formatter.
     *
     * @param xml the XML string to format
     * @return the formatted string
     */
    fun formatXml(xml: String): String? {
        assertInitialization()
        return XLog.sLogConfiguration.xmlFormatter!!.format(xml)
    }

    /**
     * Format a throwable using default throwable formatter.
     *
     * @param throwable the throwable to format
     * @return the formatted string
     */
    fun formatThrowable(throwable: Throwable): String? {
        assertInitialization()
        return XLog.sLogConfiguration.throwableFormatter!!.format(throwable)
    }

    /**
     * Format a thread using default thread formatter.
     *
     * @param thread the thread to format
     * @return the formatted string
     */
    fun formatThread(thread: Thread): String? {
        assertInitialization()
        return XLog.sLogConfiguration.threadFormatter!!.format(thread)
    }

    /**
     * Format a stack trace using default stack trace formatter.
     *
     * @param stackTrace the stack trace to format
     * @return the formatted string
     */
    fun formatStackTrace(stackTrace: Array<StackTraceElement?>?): String? {
        assertInitialization()
        return XLog.sLogConfiguration.stackTraceFormatter!!.format(stackTrace)
    }

    /**
     * Add border to string segments using default border formatter.
     *
     * @param segments the string segments to add border to
     * @return the bordered string segments
     */
    fun addBorder(segments: Array<String?>): String? {
        assertInitialization()
        return XLog.sLogConfiguration.borderFormatter!!.format(segments)
    }

    /**
     * Compress all files under the specific folder to a single zip file.
     *
     *
     * Should be call in background thread.
     *
     * @param folderPath  the specific folder path
     * @param zipFilePath the zip file path
     * @throws IOException if any error occurs
     * 
     */
    @Throws(IOException::class)
    fun compress(folderPath: String, zipFilePath: String) {
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            throw IOException("Folder $folderPath doesn't exist or isn't a directory")
        }

        val zipFile = File(zipFilePath)
        if (!zipFile.exists()) {
            val zipFolder = zipFile.parentFile
            if (!zipFolder!!.exists()) {
                if (!zipFolder.mkdirs()) {
                    throw IOException("Zip folder " + zipFolder.absolutePath + " not created")
                }
            }
            if (!zipFile.createNewFile()) {
                throw IOException("Zip file $zipFilePath not created")
            }
        }

        var bis: BufferedInputStream
        val zos = ZipOutputStream(
            BufferedOutputStream(FileOutputStream(zipFile))
        )
        try {


            val buffer = ByteArray(BUFFER_SIZE)
            val childFileList = folder.list()
            if (childFileList != null) {
                for (fileName in childFileList) {
                    if (fileName == "." || fileName == "..") {
                        continue
                    }

                    val file = File(folder, fileName)
                    if (!file.isFile) {
                        continue
                    }

                    val fis = FileInputStream(file)
                    bis = BufferedInputStream(fis, BUFFER_SIZE)
                    try {
                        val entry = ZipEntry(fileName)
                        zos.putNextEntry(entry)
                        var count: Int
                        while ((bis.read(buffer, 0, BUFFER_SIZE).also { count = it }) != -1) {
                            zos.write(buffer, 0, count)
                        }
                    } finally {
                        try {
                            bis.close()
                        } catch (e: IOException) {
                            // Ignore
                        }
                    }
                }
            }
        } finally {
            try {
                zos.close()
            } catch (e: IOException) {
                // Ignore
            }
        }
    }
}
