package com.logger.xlog.printer.file.naming

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Generate file name according to the timestamp, different dates will lead to different file names.
 */
class DateFileNameGenerator : FileNameGenerator {
    var mLocalDateFormat: ThreadLocal<SimpleDateFormat> = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US)
        }
    }

    override val isFileNameChangeable: Boolean
        get() = true

    /**
     * Generate a file name which represent a specific date.
     */
    override fun generateFileName(logLevel: Int, timestamp: Long): String {
        val sdf = mLocalDateFormat.get()
        sdf!!.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }
}
