package com.delta.core.network.logging

enum class HttpLogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
}

fun interface HttpLogSink {
    fun log(level: HttpLogLevel, tag: String, message: String)
}
