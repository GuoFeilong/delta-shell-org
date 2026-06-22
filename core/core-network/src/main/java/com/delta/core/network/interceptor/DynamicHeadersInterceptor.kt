package com.delta.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Applies static or dynamically resolved headers to every outgoing request.
 */
class DynamicHeadersInterceptor(
    private val headerProvider: () -> Map<String, String>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        headerProvider().forEach { (name, value) ->
            requestBuilder.header(name, value)
        }
        return chain.proceed(requestBuilder.build())
    }
}
