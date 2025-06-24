package com.logger.xlog.utils

import com.logger.xlog.XLog

object XLogUtil {
    fun beforeTest() {
        XLog.sIsInitialized = true
    }
}
