package com.delta.core.network.logging

import android.util.Log

/**
 * Writes HTTP logs to Logcat with line chunking (Logcat line limit ~4 KB).
 */
object AndroidLogcatHttpLogSink : HttpLogSink {
    private const val MAX_LINE_LENGTH = 4_000

    override fun log(level: HttpLogLevel, tag: String, message: String) {
        val priority = when (level) {
            HttpLogLevel.DEBUG -> Log.DEBUG
            HttpLogLevel.INFO -> Log.INFO
            HttpLogLevel.WARN -> Log.WARN
            HttpLogLevel.ERROR -> Log.ERROR
        }
        message.lineSequence().forEach { line ->
            if (line.length <= MAX_LINE_LENGTH) {
                Log.println(priority, tag, line)
            } else {
                line.chunked(MAX_LINE_LENGTH).forEach { chunk ->
                    Log.println(priority, tag, chunk)
                }
            }
        }
    }
}
