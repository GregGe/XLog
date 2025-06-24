package com.logger.xlog.formatter.stacktrace

import com.logger.xlog.internal.SystemCompat

/**
 * Formatted stack trace looks like:
 * <br></br>	├ com.logger.xlog.SampleClassC.sampleMethodC(SampleClassC.java:200)
 * <br></br>	├ com.logger.xlog.SampleClassB.sampleMethodB(SampleClassB.java:100)
 * <br></br>	└ com.logger.xlog.SampleClassA.sampleMethodA(SampleClassA.java:50)
 */
class DefaultStackTraceFormatter : StackTraceFormatter {
    override fun format(data: Array<StackTraceElement?>?): String? {
        val sb = StringBuilder(256)
        if (data.isNullOrEmpty()) {
            return null
        } else if (data.size == 1) {
            return "\t─ " + data[0].toString()
        } else {
            var i = 0
            val N = data.size
            while (i < N) {
                if (i != N - 1) {
                    sb.append("\t├ ")
                    sb.append(data[i].toString())
                    sb.append(SystemCompat.lineSeparator)
                } else {
                    sb.append("\t└ ")
                    sb.append(data[i].toString())
                }
                i++
            }
            return sb.toString()
        }
    }
}
