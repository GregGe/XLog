package com.logger.xlog.extensions

import com.logger.xlog.internal.SystemCompat

fun String?.safePlus(separator: String = SystemCompat.lineSeparator): String {
    return if (this != null) {
        "$this$separator"
    } else {
        ""
    }
}