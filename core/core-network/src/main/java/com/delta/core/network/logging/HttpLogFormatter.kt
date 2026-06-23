package com.delta.core.network.logging

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

internal object HttpLogFormatter {
    private val prettyJson = Json {
        prettyPrint = true
        isLenient = true
    }

    fun formatRequest(
        request: Request,
        config: HttpLoggingConfig,
        bodyContent: String? = null,
    ): String = buildString {
        appendLine("════════════════ HTTP REQUEST ════════════════")
        appendLine("▶ ${request.method} ${request.url.encodedPath}")
        appendLine("  URL: ${request.url}")
        if (config.logHeaders) {
            appendLine("  Headers:")
            appendHeaders(request.headers, config)
        }
        if (config.logBody && bodyContent != null) {
            appendLine("  Body:")
            appendBody(bodyContent, config)
        }
        append("══════════════════════════════════════════════")
    }

    fun formatResponse(
        response: Response,
        body: String?,
        tookMs: Long,
        config: HttpLoggingConfig,
    ): String = buildString {
        appendLine("════════════════ HTTP RESPONSE ═══════════════")
        appendLine("◀ ${response.code} ${response.message} (${tookMs}ms)")
        appendLine("  URL: ${response.request.url}")
        if (config.logHeaders) {
            appendLine("  Headers:")
            appendHeaders(response.headers, config)
        }
        if (config.logBody && body != null) {
            appendLine("  Body:")
            appendBody(body, config)
        }
        append("══════════════════════════════════════════════")
    }

    fun formatFailure(
        request: Request,
        error: Throwable,
        tookMs: Long,
    ): String = buildString {
        appendLine("════════════════ HTTP FAILURE ════════════════")
        appendLine("✖ ${request.method} ${request.url}")
        appendLine("  Error: ${error.javaClass.simpleName}: ${error.message}")
        appendLine("  Duration: ${tookMs}ms")
        append("══════════════════════════════════════════════")
    }

    fun formatBody(raw: String, config: HttpLoggingConfig): String =
        if (config.prettyPrintJson) prettyPrintIfJson(raw) else raw

    fun extractRequestBody(request: Request): RequestBodySnapshot {
        val body = request.body ?: return RequestBodySnapshot(request, null)
        if (body.isDuplex() || body.isOneShot()) {
            return RequestBodySnapshot(request, "(one-shot body omitted)")
        }
        if (bodyHasUnknownEncoding(request.headers)) {
            return RequestBodySnapshot(request, "(encoded body omitted)")
        }
        return runCatching {
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val content = buffer.readUtf8()
            val rebuiltRequest = request.newBuilder()
                .method(
                    request.method,
                    content.toRequestBody(body.contentType()),
                )
                .build()
            RequestBodySnapshot(rebuiltRequest, content)
        }.getOrElse {
            RequestBodySnapshot(request, "(failed to read request body: ${it.message})")
        }
    }

    private fun StringBuilder.appendHeaders(headers: Headers, config: HttpLoggingConfig) {
        for (index in 0 until headers.size) {
            val name = headers.name(index)
            val value = config.redactHeader(name, headers.value(index))
            appendLine("    $name: $value")
        }
    }

    private fun StringBuilder.appendBody(raw: String, config: HttpLoggingConfig) {
        formatBody(raw, config)
            .prependIndent("    ")
            .trimEnd()
            .let(::appendLine)
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val encoding = headers["Content-Encoding"] ?: return false
        return !encoding.equals("identity", ignoreCase = true) &&
            !encoding.equals("gzip", ignoreCase = true)
    }

    private fun prettyPrintIfJson(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return raw
        if (trimmed.first() !in JSON_START_CHARS) return raw
        return runCatching {
            prettyJson.encodeToString(
                JsonElement.serializer(),
                prettyJson.parseToJsonElement(trimmed),
            )
        }.getOrDefault(raw)
    }

    private val JSON_START_CHARS = charArrayOf('{', '[')
}

internal data class RequestBodySnapshot(
    val request: Request,
    val bodyContent: String?,
)
