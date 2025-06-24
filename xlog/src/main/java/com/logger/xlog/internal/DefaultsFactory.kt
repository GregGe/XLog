package com.logger.xlog.internal

import com.logger.xlog.flattener.DefaultFlattener
import com.logger.xlog.flattener.Flattener
import com.logger.xlog.formatter.border.BorderFormatter
import com.logger.xlog.formatter.border.DefaultBorderFormatter
import com.logger.xlog.formatter.message.json.DefaultJsonFormatter
import com.logger.xlog.formatter.message.json.JsonFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.formatter.message.throwable.DefaultThrowableFormatter
import com.logger.xlog.formatter.message.throwable.ThrowableFormatter
import com.logger.xlog.formatter.message.xml.DefaultXmlFormatter
import com.logger.xlog.formatter.message.xml.XmlFormatter
import com.logger.xlog.formatter.stacktrace.DefaultStackTraceFormatter
import com.logger.xlog.formatter.stacktrace.StackTraceFormatter
import com.logger.xlog.formatter.thread.DefaultThreadFormatter
import com.logger.xlog.formatter.thread.ThreadFormatter
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.file.backup.BackupStrategy
import com.logger.xlog.printer.file.backup.FileSizeBackupStrategy
import com.logger.xlog.printer.file.clean.CleanStrategy
import com.logger.xlog.printer.file.clean.NeverCleanStrategy
import com.logger.xlog.printer.file.naming.ChangelessFileNameGenerator
import com.logger.xlog.printer.file.naming.FileNameGenerator
import com.logger.xlog.printer.file.writer.SimpleWriter
import com.logger.xlog.printer.file.writer.Writer

/**
 * Factory for providing default implementation.
 */
object DefaultsFactory {
    private const val DEFAULT_LOG_FILE_NAME = "log"

    private const val DEFAULT_LOG_FILE_MAX_SIZE = 1024 * 1024L // 1M bytes;

    /**
     * Create the default JSON formatter.
     */
    @JvmStatic
    fun createJsonFormatter(): JsonFormatter {
        return DefaultJsonFormatter()
    }

    /**
     * Create the default XML formatter.
     */
    @JvmStatic
    fun createXmlFormatter(): XmlFormatter {
        return DefaultXmlFormatter()
    }

    /**
     * Create the default throwable formatter.
     */
    @JvmStatic
    fun createThrowableFormatter(): ThrowableFormatter {
        return DefaultThrowableFormatter()
    }

    /**
     * Create the default thread formatter.
     */
    @JvmStatic
    fun createThreadFormatter(): ThreadFormatter {
        return DefaultThreadFormatter()
    }

    /**
     * Create the default stack trace formatter.
     */
    @JvmStatic
    fun createStackTraceFormatter(): StackTraceFormatter {
        return DefaultStackTraceFormatter()
    }

    /**
     * Create the default border formatter.
     */
    @JvmStatic
    fun createBorderFormatter(): BorderFormatter {
        return DefaultBorderFormatter()
    }

    /**
     * Create the default [Flattener].
     */
    @JvmStatic
    fun createFlattener(): Flattener {
        return DefaultFlattener()
    }

    /**
     * Create the default printer.
     */
    fun createPrinter(): Printer {
        return Platform.get().defaultPrinter()
    }

    /**
     * Create the default file name generator for [FilePrinter].
     */
    @JvmStatic
    fun createFileNameGenerator(): FileNameGenerator {
        return ChangelessFileNameGenerator(DEFAULT_LOG_FILE_NAME)
    }

    /**
     * Create the default backup strategy for [FilePrinter].
     */
    @JvmStatic
    fun createBackupStrategy(): BackupStrategy {
        return FileSizeBackupStrategy(DEFAULT_LOG_FILE_MAX_SIZE)
    }

    /**
     * Create the default clean strategy for [FilePrinter].
     */
    @JvmStatic
    fun createCleanStrategy(): CleanStrategy {
        return NeverCleanStrategy()
    }

    /**
     * Create the default writer for [FilePrinter].
     */
    @JvmStatic
    fun createWriter(): Writer {
        return SimpleWriter()
    }

    /**
     * Get the builtin object formatters.
     *
     * @return the builtin object formatters
     */
    @JvmStatic
    fun builtinObjectFormatters(): MutableMap<Class<*>, ObjectFormatter<*>> {
        return Platform.get().builtinObjectFormatters()
    }
}
