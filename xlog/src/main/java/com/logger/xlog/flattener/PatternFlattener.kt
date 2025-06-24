package com.logger.xlog.flattener

import com.logger.xlog.LogLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

/**
 * Flatten the log using the format specified by pattern.
 *
 *
 * Supported parameters:
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Supported parameters">
 * <tr bgcolor="#ccccff">
 * <th align=left>Parameter
</th> * <th align=left>Represents
</th></tr> * <tr>
 * <td>{d}
</td> * <td>Date in default date format {@value #DEFAULT_DATE_FORMAT}
</td></tr> * <tr>
 * <td>{d format}
</td> * <td>Date in specific date format
</td></tr> * <tr>
 * <td>{l}
</td> * <td>Short name of log level. e.g: V/D/I
</td></tr> * <tr>
 * <td>{L}
</td> * <td>Long name of log level. e.g: VERBOSE/DEBUG/INFO
</td></tr> * <tr>
 * <td>{t}
</td> * <td>Tag of log
</td></tr> * <tr>
 * <td>{m}
</td> * <td>Message of log
</td></tr></table> *
</blockquote> *
 *
 *
 * Imagine there is a log, with [LogLevel.DEBUG] level, "my_tag" tag and "Simple message"
 * message, the flattened log would be as below.
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Examples of patterns and flattened logs">
 * <tr bgcolor="#ccccff">
 * <th align=left>Pattern
</th> * <th align=left>Flattened log
</th></tr> * <tr>
 * <td>{d} {l}/{t}: {m}
</td> * <td>2016-11-30 13:00:00.000 D/my_tag: Simple message
</td></tr> * <tr>
 * <td>{d yyyy-MM-dd HH:mm:ss.SSS} {l}/{t}: {m}
</td> * <td>2016-11-30 13:00:00.000 D/my_tag: Simple message
</td></tr> * <tr>
 * <td>{d yyyy/MM/dd HH:mm:ss} {l}|{t}: {m}
</td> * <td>2016/11/30 13:00:00 D|my_tag: Simple message
</td></tr> * <tr>
 * <td>{d yy/MM/dd HH:mm:ss} {l}|{t}: {m}
</td> * <td>16/11/30 13:00:00 D|my_tag: Simple message
</td></tr> * <tr>
 * <td>{d MM/dd HH:mm} {l}-{t}-{m}
</td> * <td>11/30 13:00 D-my_tag-Simple message
</td></tr></table> *
</blockquote> *
 *
 * 
 */
open class PatternFlattener(private val pattern: String) : Flattener {

    private val parameterFillers: List<ParameterFiller>

    /**
     * Constructor.
     *
     * @param pattern the format pattern to flatten the log
     */
    init {

        val parameters = parsePattern(pattern)
        parameterFillers = parseParameters(parameters)
        require(parameterFillers.isNotEmpty()) {
            ("No recognizable parameter found in the pattern "
                    + pattern)
        }
    }

    override fun flatten(
        timeMillis: Long,
        logLevel: Int,
        tag: String,
        message: String
    ): CharSequence {
        var flattenedLog = pattern
        for (parameterFiller in parameterFillers) {
            flattenedLog = parameterFiller.fill(flattenedLog, timeMillis, logLevel, tag, message)
        }
        return flattenedLog
    }

    /**
     * Fill the original pattern string with formatted date string.
     */
    class DateFiller(
        wrappedParameter: String?,
        trimmedParameter: String?,
        @JvmField var dateFormat: String
    ) :
        ParameterFiller(wrappedParameter, trimmedParameter) {
        private val threadLocalDateFormat: ThreadLocal<SimpleDateFormat> =
            object : ThreadLocal<SimpleDateFormat>() {
                override fun initialValue(): SimpleDateFormat {
                    return SimpleDateFormat(dateFormat, Locale.US)
                }
            }

        init {
            try {
                // Test the format, will throw an exception if it is a bad format.
                threadLocalDateFormat.get()?.format(Date())
            } catch (e: Exception) {
                throw IllegalArgumentException("Bad date pattern: $dateFormat", e)
            }
        }

        override fun fill(
            pattern: String,
            timeMillis: Long,
            logLevel: Int,
            tag: String,
            message: String
        ): String {
            return pattern.replace(
                wrappedParameter!!,
                threadLocalDateFormat.get()?.format(Date(timeMillis)) ?: ""
            )
        }
    }

    /**
     * Fill the original pattern string with level name.
     */
    class LevelFiller(
        wrappedParameter: String?,
        trimmedParameter: String?,
        @JvmField var useLongName: Boolean
    ) :
        ParameterFiller(wrappedParameter, trimmedParameter) {
        override fun fill(
            pattern: String,
            timeMillis: Long,
            logLevel: Int,
            tag: String,
            message: String
        ): String {
            return if (useLongName) {
                pattern.replace(
                    wrappedParameter!!,
                    LogLevel.getLevelName(logLevel)
                )
            } else {
                pattern.replace(
                    wrappedParameter!!,
                    LogLevel.getShortLevelName(logLevel)
                )
            }
        }
    }

    /**
     * Fill the original pattern string with tag.
     */
    class TagFiller(wrappedParameter: String?, trimmedParameter: String?) :
        ParameterFiller(wrappedParameter, trimmedParameter) {
        override fun fill(
            pattern: String,
            timeMillis: Long,
            logLevel: Int,
            tag: String,
            message: String
        ): String {
            return pattern.replace(wrappedParameter!!, tag)
        }
    }

    /**
     * Fill the original pattern string with message.
     */
    class MessageFiller(wrappedParameter: String?, trimmedParameter: String?) :
        ParameterFiller(wrappedParameter, trimmedParameter) {
        override fun fill(
            pattern: String,
            timeMillis: Long,
            logLevel: Int,
            tag: String,
            message: String
        ): String {
            return pattern.replace(wrappedParameter!!, message)
        }
    }

    /**
     * Fill the original pattern string with the value of parameter.
     */
    abstract class ParameterFiller(
        /**
         * The parameter parsed from the original pattern string, in a format of "{parameter}", maybe
         * spaces around the parameter and within the "{" and "}".
         */
        var wrappedParameter: String?,
        /**
         * The trimmed parameter, and without "{" and "}" around it.
         */
        var trimmedParameter: String?
    ) {
        /**
         * Fill the original pattern string with the value of parameter.
         *
         * @param pattern    the original pattern
         * @param timeMillis the time milliseconds of log
         * @param logLevel   the log level of flattening log
         * @param tag        the tag of flattening log
         * @param message    the message of the flattening log
         * @return the filled pattern string
         */
        abstract fun fill(
            pattern: String,
            timeMillis: Long,
            logLevel: Int,
            tag: String,
            message: String
        ): String
    }

    companion object {
        private const val PARAM = "[^{}]*"
        private val PARAM_REGEX: Pattern = Pattern.compile("\\{(" + PARAM + ")\\}")

        private const val PARAMETER_DATE = "d"
        private const val PARAMETER_LEVEL_SHORT = "l"
        private const val PARAMETER_LEVEL_LONG = "L"
        private const val PARAMETER_TAG = "t"
        private const val PARAMETER_MESSAGE = "m"

        const val DEFAULT_DATE_FORMAT: String = "yyyy-MM-dd HH:mm:ss.SSS"

        /**
         * Get the list of parameters from the given pattern.
         *
         * @param pattern the given pattern
         * @return the parameters list, or empty if no parameter found from the given pattern
         */
        @JvmStatic
        fun parsePattern(pattern: String): List<String> {
            val parameters: MutableList<String> = ArrayList(4)
            val matcher = PARAM_REGEX.matcher(pattern)
            while (matcher.find()) {
                parameters.add(matcher.group(1)!!)
            }
            return parameters
        }

        /**
         * Get the list of parameter fillers from the given parameters.
         *
         * @param parameters the given parameters
         * @return the parameter fillers, or empty if none of the parameters is recognizable
         */
        private fun parseParameters(parameters: List<String>): List<ParameterFiller> {
            val parameterFillers: MutableList<ParameterFiller> = ArrayList(parameters.size)
            for (parameter in parameters) {
                val parameterFiller = parseParameter(parameter)
                if (parameterFiller != null) {
                    parameterFillers.add(parameterFiller)
                }
            }
            return parameterFillers
        }

        /**
         * Create a parameter filler if the given parameter is recognizable.
         *
         * @param parameter the given parameter
         * @return the created parameter filler, or null if can not recognize the given parameter
         */
        private fun parseParameter(parameter: String): ParameterFiller? {
            val wrappedParameter = "{$parameter}"
            val trimmedParameter = parameter.trim { it <= ' ' }
            var parameterFiller: ParameterFiller? =
                parseDateParameter(wrappedParameter, trimmedParameter)
            if (parameterFiller != null) {
                return parameterFiller
            }

            parameterFiller = parseLevelParameter(wrappedParameter, trimmedParameter)
            if (parameterFiller != null) {
                return parameterFiller
            }

            parameterFiller = parseTagParameter(wrappedParameter, trimmedParameter)
            if (parameterFiller != null) {
                return parameterFiller
            }

            parameterFiller = parseMessageParameter(wrappedParameter, trimmedParameter)
            if (parameterFiller != null) {
                return parameterFiller
            }

            return null
        }

        /**
         * Try to create a date filler if the given parameter is a date parameter.
         *
         * @return created date filler, or null if the given parameter is not a date parameter
         */
        @JvmStatic
        fun parseDateParameter(wrappedParameter: String?, trimmedParameter: String): DateFiller? {
            if (trimmedParameter.startsWith(PARAMETER_DATE + " ")
                && trimmedParameter.length > PARAMETER_DATE.length + 1
            ) {
                val dateFormat = trimmedParameter.substring(PARAMETER_DATE.length + 1)
                return DateFiller(wrappedParameter, trimmedParameter, dateFormat)
            } else if (trimmedParameter == PARAMETER_DATE) {
                return DateFiller(wrappedParameter, trimmedParameter, DEFAULT_DATE_FORMAT)
            }
            return null
        }

        /**
         * Try to create a level filler if the given parameter is a level parameter.
         *
         * @return created level filler, or null if the given parameter is not a level parameter
         */
        @JvmStatic
        fun parseLevelParameter(wrappedParameter: String?, trimmedParameter: String): LevelFiller? {
            if (trimmedParameter == PARAMETER_LEVEL_SHORT) {
                return LevelFiller(wrappedParameter, trimmedParameter, false)
            } else if (trimmedParameter == PARAMETER_LEVEL_LONG) {
                return LevelFiller(wrappedParameter, trimmedParameter, true)
            }
            return null
        }

        /**
         * Try to create a tag filler if the given parameter is a tag parameter.
         *
         * @return created tag filler, or null if the given parameter is not a tag parameter
         */
        @JvmStatic
        fun parseTagParameter(wrappedParameter: String?, trimmedParameter: String): TagFiller? {
            if (trimmedParameter == PARAMETER_TAG) {
                return TagFiller(wrappedParameter, trimmedParameter)
            }
            return null
        }

        /**
         * Try to create a message filler if the given parameter is a message parameter.
         *
         * @return created message filler, or null if the given parameter is not a message parameter
         */
        @JvmStatic
        fun parseMessageParameter(
            wrappedParameter: String?,
            trimmedParameter: String
        ): MessageFiller? {
            if (trimmedParameter == PARAMETER_MESSAGE) {
                return MessageFiller(wrappedParameter, trimmedParameter)
            }
            return null
        }
    }
}
