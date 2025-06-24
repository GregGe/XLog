package com.logger.xlog.sample

import android.app.Application
import android.os.Build
import com.logger.xlog.LogConfiguration
import com.logger.xlog.LogLevel
import com.logger.xlog.XLog
import com.logger.xlog.flattener.ClassicFlattener
import com.logger.xlog.interceptor.AndroidReleaseInterceptor
import com.logger.xlog.interceptor.AllowAllTagsFilterInterceptor
import com.logger.xlog.printer.AndroidPrinter
import com.logger.xlog.printer.Printer
import com.logger.xlog.printer.file.FilePrinter
import com.logger.xlog.printer.file.naming.DateFileNameGenerator
import com.logger.xlog.printer.file.writer.SimpleWriter
import java.io.File


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initXlog()
    }

    /**
     * Initialize XLog.
     */
    private fun initXlog() {
        val globalTag = getString(R.string.global_tag)
        val config = LogConfiguration.Builder()
            .logLevel(
                if (BuildConfig.DEBUG)
                    LogLevel.ALL // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                else
                    LogLevel.WARN
            )
            .tag(globalTag) // Specify TAG, default: "X-LOG"
            // .enableThreadInfo()                                 // Enable thread info, disabled by default
            // .enableStackTrace(2)                                // Enable stack trace info with depth 2, disabled by default
            // .enableBorder()                                     // Enable border, disabled by default
            // .jsonFormatter(new MyJsonFormatter())               // Default: DefaultJsonFormatter
            // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
            // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
            // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
            // .stackTraceFormatter(new MyStackTraceFormatter())   // Default: DefaultStackTraceFormatter
            // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
            // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
            //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
            .addInterceptor(
                if (BuildConfig.DEBUG) {
                     AllowAllTagsFilterInterceptor()
//                    AndroidReleaseInterceptor(
//                        "$globalTag-all",
//                        listOf(globalTag, "test"),
//                        LogLevel.ERROR
//                    )
                } else {
                    AndroidReleaseInterceptor(
                        "$globalTag-all",
                        listOf(globalTag, "test"),
                        LogLevel.ERROR
                    )
                }
            ) // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
            //     "whitelist1", "whitelist2", "whitelist3"))
            // .addInterceptor(new MyInterceptor())                // Add a log interceptor
            .build()

        val androidPrinter: Printer =
            AndroidPrinter() // Printer that print the log using android.util.Log
        val filePrinter: Printer = FilePrinter.Builder(
            File(
                externalCacheDir!!.absolutePath, "log"
            ).path
        ) // Specify the path to save log file
            .fileNameGenerator(DateFileNameGenerator()) // Default: ChangelessFileNameGenerator("log")
            // .backupStrategy(new MyBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
            // .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
            .flattener(ClassicFlattener()) // Default: DefaultFlattener
            .writer(object : SimpleWriter() {
                // Default: SimpleWriter
                override fun onNewFileCreated(file: File?) {
                    super.onNewFileCreated(file)
                    val header = """
                        >>>>>>>>>>>>>>>> File Header >>>>>>>>>>>>>>>>
                        Device Manufacturer: ${Build.MANUFACTURER}
                        Device Model       : ${Build.MODEL}
                        Android Version    : ${Build.VERSION.RELEASE}
                        Android SDK        : ${Build.VERSION.SDK_INT}
                        App VersionName    : ${BuildConfig.VERSION_NAME}
                        App VersionCode    : ${BuildConfig.VERSION_CODE}
                        <<<<<<<<<<<<<<<< File Header <<<<<<<<<<<<<<<<
                        """.trimIndent()
                    appendLog(header)
                }
            })
            .build()

        XLog.init( // Initialize XLog
            config,  // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
            androidPrinter,  // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
            filePrinter
        )

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter
    }

    companion object {
        lateinit var globalFilePrinter: Printer
    }
}