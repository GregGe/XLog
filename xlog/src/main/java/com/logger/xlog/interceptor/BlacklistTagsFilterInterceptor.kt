package com.logger.xlog.interceptor

import com.logger.xlog.LogItem

/**
 * Filter out the logs with a tag that is in the blacklist.
 *
 */
class BlacklistTagsFilterInterceptor(private val blacklistTags: Iterable<String>) :
    AbstractFilterInterceptor() {

    /**
     * Constructor
     *
     * @param blacklistTags the blacklist tags, the logs with a tag that is in the blacklist will be
     * filtered out
     */
    constructor(vararg blacklistTags: String) : this(listOf<String>(*blacklistTags))

    /**
     * {@inheritDoc}
     *
     * @return true if the tag of the log is in the blacklist, false otherwise
     */
    override fun reject(log: LogItem): Boolean {
        for (disabledTag in blacklistTags) {
            if (log.tag == disabledTag) {
                return true
            }
        }
        return false
    }
}
