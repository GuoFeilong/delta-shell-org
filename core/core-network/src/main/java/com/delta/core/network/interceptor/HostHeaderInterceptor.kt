package com.delta.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Forces a specific HTTP `Host` header, independent of the request URL.
 */
class HostHeaderInterceptor(
    private val host: String,
) : Interceptor {
    init {
        require(host.isNotBlank()) { "host must not be blank" }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(HEADER_HOST, host)
            .build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_HOST = "Host"
    }
}
