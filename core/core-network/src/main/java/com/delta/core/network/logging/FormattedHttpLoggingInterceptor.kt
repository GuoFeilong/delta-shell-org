package com.delta.core.network.logging

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response

/**
 * Debug-only HTTP logger with JSON pretty-print and a dedicated Logcat tag.
 *
 * Filter in Android Studio Logcat: `tag:DeltaHttp`
 */
class FormattedHttpLoggingInterceptor(
    private val config: HttpLoggingConfig = HttpLoggingConfig.DEFAULT,
    private val logSink: HttpLogSink = AndroidLogcatHttpLogSink,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (!config.shouldLogPath(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        val startNs = System.nanoTime()
        val requestSnapshot = if (config.logBody) {
            HttpLogFormatter.extractRequestBody(originalRequest)
        } else {
            RequestBodySnapshot(originalRequest, null)
        }
        logSink.log(
            HttpLogLevel.DEBUG,
            config.tag,
            HttpLogFormatter.formatRequest(
                request = requestSnapshot.request,
                config = config,
                bodyContent = requestSnapshot.bodyContent,
            ),
        )

        val response = try {
            chain.proceed(requestSnapshot.request)
        } catch (error: Exception) {
            val tookMs = elapsedMs(startNs)
            logSink.log(
                HttpLogLevel.ERROR,
                config.tag,
                HttpLogFormatter.formatFailure(requestSnapshot.request, error, tookMs),
            )
            throw error
        }

        val tookMs = elapsedMs(startNs)
        val peekedBody = peekResponseBody(response)
        logSink.log(
            HttpLogLevel.DEBUG,
            config.tag,
            HttpLogFormatter.formatResponse(response, peekedBody, tookMs, config),
        )
        return response
    }

    private fun peekResponseBody(response: Response): String? {
        if (!config.logBody) return null
        val body = response.body ?: return null
        if (bodyHasUnknownEncoding(response.headers)) {
            return "(encoded body omitted)"
        }
        val contentType = body.contentType()
        if (contentType != null && !isPlaintext(contentType)) {
            return "(binary ${contentType.type}/${contentType.subtype} body omitted)"
        }
        return runCatching {
            response.peekBody(MAX_PEEK_BODY_BYTES).string()
        }.getOrElse { "(failed to read response body: ${it.message})" }
    }

    private fun bodyHasUnknownEncoding(headers: okhttp3.Headers): Boolean {
        val encoding = headers["Content-Encoding"] ?: return false
        return !encoding.equals("identity", ignoreCase = true) &&
            !encoding.equals("gzip", ignoreCase = true)
    }

    private fun isPlaintext(mediaType: MediaType): Boolean {
        if (mediaType.type == "text") return true
        val subtype = mediaType.subtype.lowercase()
        return subtype.contains("json") ||
            subtype.contains("xml") ||
            subtype.contains("x-www-form-urlencoded") ||
            subtype.endsWith("+json") ||
            subtype.endsWith("+xml")
    }

    private fun elapsedMs(startNs: Long): Long =
        (System.nanoTime() - startNs) / 1_000_000L

    companion object {
        private const val MAX_PEEK_BODY_BYTES = 1024L * 1024L
    }
}
