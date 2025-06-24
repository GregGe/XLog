package com.logger.xlog.formatter.border

import com.logger.xlog.formatter.Formatter

/**
 * The border formatter used to wrap string segments with borders when logging.
 *
 *
 * e.g:
 * <br></br>
 * <br></br>╔════════════════════════════════════════════════════════════════════════════
 * <br></br>║Thread: main
 * <br></br>╟────────────────────────────────────────────────────────────────────────────
 * <br></br>║	├ com.logger.xlog.SampleClassB.sampleMethodB(SampleClassB.java:100)
 * <br></br>║	└ com.logger.xlog.SampleClassA.sampleMethodA(SampleClassA.java:50)
 * <br></br>╟────────────────────────────────────────────────────────────────────────────
 * <br></br>║Here is a simple message
 * <br></br>╚════════════════════════════════════════════════════════════════════════════
 */
interface BorderFormatter : Formatter<Array<String?>?>
