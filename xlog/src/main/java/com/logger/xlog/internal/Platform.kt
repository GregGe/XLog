package com.logger.xlog.internal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.logger.xlog.formatter.message.obj.BundleFormatter
import com.logger.xlog.formatter.message.obj.IntentFormatter
import com.logger.xlog.formatter.message.obj.ObjectFormatter
import com.logger.xlog.printer.AndroidPrinter
import com.logger.xlog.printer.ConsolePrinter
import com.logger.xlog.printer.Printer

open class Platform {

    open fun lineSeparator(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            System.lineSeparator()
        } else {
            UNIX_SYSTEM_LINE_SEPARATOR
        }
    }

    open fun defaultPrinter(): Printer {
        return ConsolePrinter()
    }

    open fun builtinObjectFormatters(): MutableMap<Class<*>, ObjectFormatter<*>> {
        return mutableMapOf()
    }

    open fun warn(msg: String?) {
        println(msg)
    }

    open fun error(msg: String?) {
        println(msg)
    }

    internal class Android : Platform() {
        override fun lineSeparator(): String {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return "\n"
            }
            return System.lineSeparator()
        }

        override fun defaultPrinter(): Printer {
            return AndroidPrinter()
        }

        override fun builtinObjectFormatters(): MutableMap<Class<*>, ObjectFormatter<*>> {
            return BUILTIN_OBJECT_FORMATTERS.toMutableMap()
        }

        override fun warn(msg: String?) {
            Log.w("XLog", msg ?: "")
        }

        override fun error(msg: String?) {
            Log.e("XLog", msg ?: "")
        }

        companion object {
            private val BUILTIN_OBJECT_FORMATTERS: Map<Class<*>, ObjectFormatter<*>>

            init {
                val objectFormatters: MutableMap<Class<*>, ObjectFormatter<*>> =
                    HashMap()
                objectFormatters[Bundle::class.java] = BundleFormatter()
                objectFormatters[Intent::class.java] = IntentFormatter()
                BUILTIN_OBJECT_FORMATTERS = objectFormatters.toMap()
            }
        }
    }

    companion object {
        const val UNIX_SYSTEM_LINE_SEPARATOR = "\n"
        private val PLATFORM = findPlatform()

        @JvmStatic
        fun get(): Platform {
            return PLATFORM
        }

        @SuppressLint("ObsoleteSdkInt")
        private fun findPlatform(): Platform {
            try {
                Class.forName("android.os.Build")
                if (Build.VERSION.SDK_INT != 0) {
                    return Android()
                }
            } catch (ignored: ClassNotFoundException) {
            }
            return Platform()
        }
    }
}
