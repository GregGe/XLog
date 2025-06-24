package com.logger.xlog.formatter.border

import com.logger.xlog.internal.SystemCompat

/**
 * String segments wrapped with borders look like:
 * <br></br>╔════════════════════════════════════════════════════════════════════════════
 * <br></br>║String segment 1
 * <br></br>╟────────────────────────────────────────────────────────────────────────────
 * <br></br>║String segment 2
 * <br></br>╟────────────────────────────────────────────────────────────────────────────
 * <br></br>║String segment 3
 * <br></br>╚════════════════════════════════════════════════════════════════════════════
 */
class DefaultBorderFormatter : BorderFormatter {
    override fun format(data: Array<String?>?): String {
        if (data.isNullOrEmpty()) {
            return ""
        }

        val nonNullSegments = data.filterNotNull()
        val nonNullCount = nonNullSegments.size
        val msgBuilder = StringBuilder()
        msgBuilder.append(SystemCompat.lineSeparator)
            .append(TOP_HORIZONTAL_BORDER)
            .append(SystemCompat.lineSeparator)
        for (i in 0 until nonNullCount) {
            msgBuilder.append(appendVerticalBorder(nonNullSegments[i]))
            if (i != nonNullCount - 1) {
                msgBuilder.append(SystemCompat.lineSeparator).append(DIVIDER_HORIZONTAL_BORDER)
                    .append(SystemCompat.lineSeparator)
            } else {
                msgBuilder.append(SystemCompat.lineSeparator).append(BOTTOM_HORIZONTAL_BORDER)
            }
        }
        return msgBuilder.toString()
    }

    companion object {
        private const val VERTICAL_BORDER_CHAR = '║'

        // Length: 100.
        private const val TOP_HORIZONTAL_BORDER =
            "╔═════════════════════════════════════════════════" +
                    "══════════════════════════════════════════════════"

        // Length: 99.
        private const val DIVIDER_HORIZONTAL_BORDER =
            "╟─────────────────────────────────────────────────" +
                    "──────────────────────────────────────────────────"

        // Length: 100.
        private const val BOTTOM_HORIZONTAL_BORDER =
            "╚═════════════════════════════════════════════════" +
                    "══════════════════════════════════════════════════"

        /**
         * Add {@value #VERTICAL_BORDER_CHAR} to each line of msg.
         *
         * @param msg the message to add border
         * @return the message with {@value #VERTICAL_BORDER_CHAR} in the start of each line
         */
        private fun appendVerticalBorder(msg: String): String {
            val borderedMsgBuilder = StringBuilder(msg.length + 10)
            val lines =
                msg.split(SystemCompat.lineSeparator.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            var i = 0
            val N = lines.size
            while (i < N) {
                if (i != 0) {
                    borderedMsgBuilder.append(SystemCompat.lineSeparator)
                }
                val line = lines[i]
                borderedMsgBuilder.append(VERTICAL_BORDER_CHAR).append(line)
                i++
            }
            return borderedMsgBuilder.toString()
        }
    }
}
