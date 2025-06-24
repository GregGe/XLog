package com.logger.xlog.formatter.stacktrace

import com.logger.xlog.formatter.Formatter

/**
 * The stack trace formatter used to format the stack trace when logging.
 */
interface StackTraceFormatter : Formatter<Array<StackTraceElement?>?>
