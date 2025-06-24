package com.logger.xlog.interceptor

import com.logger.xlog.LogItem

class AllowAllTagsFilterInterceptor : AbstractFilterInterceptor() {
    override fun reject(log: LogItem): Boolean {
        return false
    }
}