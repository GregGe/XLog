package com.logger.xlog.formatter.message.json

import com.logger.xlog.internal.Platform
import org.json.JSONArray
import org.json.JSONObject

/**
 * Simply format the JSON using [JSONObject] and [JSONArray].
 */
class DefaultJsonFormatter : JsonFormatter {
    override fun format(data: String?): String {
        val formattedString: String?
        if (data == null || data.trim { it <= ' ' }.isEmpty()) {
            Platform.get().warn("JSON empty.")
            return ""
        }
        try {
            if (data.startsWith("{")) {
                val jsonObject = JSONObject(data)
                formattedString = jsonObject.toString(JSON_INDENT)
            } else if (data.startsWith("[")) {
                val jsonArray = JSONArray(data)
                formattedString = jsonArray.toString(JSON_INDENT)
            } else {
                Platform.get().warn("JSON should start with { or [")
                return data
            }
        } catch (e: Exception) {
            Platform.get().warn(e.message)
            return data
        }
        return formattedString
    }

    companion object {
        private const val JSON_INDENT = 4
    }
}
