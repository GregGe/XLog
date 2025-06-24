package com.logger.xlog

import com.logger.xlog.LogLevel.VERBOSE
import com.logger.xlog.printer.Printer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class ConcurrentTest {
    @Test
    fun printLogsConcurrently() {
        // 5 printers and 5 containers.

        val logsContainer1: MutableList<SequencedLog> = ArrayList()
        val logsContainer2: MutableList<SequencedLog> = ArrayList()
        val logsContainer3: MutableList<SequencedLog> = ArrayList()
        val logsContainer4: MutableList<SequencedLog> = ArrayList()
        val logsContainer5: MutableList<SequencedLog> = ArrayList()

        val printer1 = ThreadSafePrinter(logsContainer1)
        val printer2 = ThreadSafePrinter(logsContainer2)
        val printer3 = ThreadSafePrinter(logsContainer3)
        val printer4 = ThreadSafePrinter(logsContainer4)
        val printer5 = ThreadSafePrinter(logsContainer5)

        XLog.init(VERBOSE, printer1, printer2, printer3, printer4, printer5)

        // 4 threads print logs concurrently.
        val t1Done = AtomicBoolean(false)
        val t2Done = AtomicBoolean(false)
        val t3Done = AtomicBoolean(false)
        val t4Done = AtomicBoolean(false)

        val logsCount = 2000
        Thread {
            for (i in 0 until logsCount) {
                XLog.i("t1 $i")
                XLog.i("t1 $i")
                XLog.i("t1 $i")
            }
            t1Done.set(true)
        }.start()

        Thread {
            for (j in 0 until logsCount) {
                XLog.d("t2 $j")
            }
            t2Done.set(true)
        }.start()

        Thread {
            for (k in 0 until logsCount) {
                XLog.d("t3 $k")
                XLog.d("t3 $k")
            }
            t3Done.set(true)
        }.start()

        Thread {
            for (k in 0 until logsCount) {
                XLog.d("t4 $k")
                XLog.d("t4 $k")
                XLog.d("t4 $k")
            }
            t4Done.set(true)
        }.start()

        // Wait until done.
        while (!t1Done.get() || !t2Done.get() || !t3Done.get() || !t4Done.get());

        // Assert logs number in all containers.
        assertEquals(logsContainer1.size, logsContainer2.size)
        assertEquals(logsContainer1.size, logsContainer3.size)
        assertEquals(logsContainer1.size, logsContainer4.size)
        assertEquals(logsContainer1.size, logsContainer5.size)

        // Assert logs content in all containers.
        val size = logsContainer1.size
        for (i in 0 until size) {
            assertLog(logsContainer1[i], logsContainer2[i])
            assertLog(logsContainer1[i], logsContainer3[i])
            assertLog(logsContainer1[i], logsContainer4[i])
            assertLog(logsContainer1[i], logsContainer5[i])
        }
    }

    private fun assertLog(expected: SequencedLog, log: SequencedLog) {
        assertTrue("Expect $expected but found $log", expected.seq == log.seq)
    }

    private class ThreadSafePrinter(private val logsContainers: MutableList<SequencedLog>) :
        Printer {
        var seq: Int = 0

        override fun println(logLevel: Int, tag: String, msg: String) {
            synchronized(this) {
                logsContainers.add(SequencedLog(seq++, msg))
            }
        }
    }

    private class SequencedLog(var seq: Int, var msg: String) {
        override fun toString(): String {
            return "seq: $seq, msg: $msg"
        }
    }
}