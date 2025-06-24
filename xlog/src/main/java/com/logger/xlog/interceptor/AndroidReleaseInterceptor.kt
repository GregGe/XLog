package com.logger.xlog.interceptor

import android.util.Log
import com.logger.xlog.LogItem
import com.logger.xlog.LogLevel

class AndroidReleaseInterceptor(
    private val allTag: String,
    private val tags: List<String>,
    private val logLevel: Int = LogLevel.INFO
) : AbstractFilterInterceptor() {
    private val tagMap: MutableMap<String, Boolean> = mutableMapOf()

    init {
        refreshAllTags()
    }

    override fun reject(log: LogItem): Boolean {
        if (tagMap[allTag] == true) {
            return false
        }

        val enable = tagMap[log.tag] ?: false
        if (enable) {
            return log.level < logLevel
        }
        return true
    }

    fun refreshAllTags() {
        tagMap.clear()
        tagMap[allTag] = isPropertyEnabled(allTag)
        tags.forEach {
            tagMap[it] = isPropertyEnabled(it)
        }
    }

    /**
     * Enable/Disable the log of tag.
     *
     * adb shell setprop log.tag.PROPERTY_NAME [VERBOSE | SUPPRESS]
     * adb shell getprop log.tag.PROPERTY_NAME
     *
     * Enable/Disable the log of different level.
     *
     * adb shell setprop log.tag V
     * adb shell getprop log.tag   --> results: [log.tag]: [I]
     *
     * @param propertyName tag
     * @return This tag is enable or not
     */
    private fun isPropertyEnabled(propertyName: String?): Boolean {
        return Log.isLoggable(propertyName, Log.VERBOSE)
    }
}