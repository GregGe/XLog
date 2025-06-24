package com.logger.xlog.interceptor

import com.logger.xlog.LogItem

/**
 * Filter out the logs with a tag that is NOT in the whitelist.
 *
 * 
 */
class WhitelistTagsFilterInterceptor(private val whitelistTags: Iterable<String>) :
    AbstractFilterInterceptor() {

    /**
     * Constructor
     *
     * @param whitelistTags the whitelist tags, the logs with a tag that is NOT in the whitelist
     * will be filtered out
     */
    constructor(vararg whitelistTags: String) : this(listOf<String>(*whitelistTags))

    /**
     * {@inheritDoc}
     *
     * @return true if the tag of the log is NOT in the whitelist, false otherwise
     */
    override fun reject(log: LogItem): Boolean {
        for (enabledTag in whitelistTags) {
            if (log.tag == enabledTag) {
                return false
            }
        }
        return true
    }
}
