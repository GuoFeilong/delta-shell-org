package com.delta.core.network.factory

import com.delta.core.network.config.NetworkConfig
import com.delta.core.network.interceptor.DynamicHeadersInterceptor
import com.delta.core.network.interceptor.HostHeaderInterceptor
import com.delta.core.network.logging.FormattedHttpLoggingInterceptor
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit

/**
 * Creates OkHttp/Retrofit clients from a [NetworkConfig].
 *
 * Business modules depend on `core-network`, define their own [NetworkConfig],
 * optionally add auth interceptors, then call [createApiService].
 */
object NetworkClientFactory {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val jsonMediaType = "application/json".toMediaType()

    fun createOkHttpClient(
        config: NetworkConfig,
        extraInterceptors: List<Interceptor> = emptyList(),
        headerProvider: (() -> Map<String, String>)? = null,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)

        if (config.headers.isNotEmpty() || headerProvider != null) {
            builder.addInterceptor(
                DynamicHeadersInterceptor {
                    buildMap {
                        putAll(config.headers)
                        headerProvider?.invoke()?.let(::putAll)
                    }
                },
            )
        }

        config.hostHeader?.let { host ->
            builder.addInterceptor(HostHeaderInterceptor(host))
        }

        extraInterceptors.forEach(builder::addInterceptor)

        if (config.loggingEnabled) {
            builder.addInterceptor(
                FormattedHttpLoggingInterceptor(config.httpLoggingConfig),
            )
        }

        return builder.build()
    }

    fun createRetrofit(
        config: NetworkConfig,
        okHttpClient: OkHttpClient? = null,
        extraInterceptors: List<Interceptor> = emptyList(),
        headerProvider: (() -> Map<String, String>)? = null,
    ): Retrofit {
        val client = okHttpClient ?: createOkHttpClient(
            config = config,
            extraInterceptors = extraInterceptors,
            headerProvider = headerProvider,
        )

        return Retrofit.Builder()
            .baseUrl(config.normalizedBaseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(jsonMediaType))
            .build()
    }

    inline fun <reified T> createApiService(
        config: NetworkConfig,
        okHttpClient: OkHttpClient? = null,
        extraInterceptors: List<Interceptor> = emptyList(),
        noinline headerProvider: (() -> Map<String, String>)? = null,
    ): T = createRetrofit(
        config = config,
        okHttpClient = okHttpClient,
        extraInterceptors = extraInterceptors,
        headerProvider = headerProvider,
    ).create()
}
