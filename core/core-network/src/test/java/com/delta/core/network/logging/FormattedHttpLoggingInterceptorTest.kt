package com.delta.core.network.logging

import com.delta.core.network.config.networkConfig
import com.delta.core.network.factory.NetworkClientFactory
import kotlinx.serialization.Serializable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.http.Body
import retrofit2.http.POST

class FormattedHttpLoggingInterceptorTest {
    private lateinit var server: MockWebServer
    private val logs = mutableListOf<String>()

    private val logSink = HttpLogSink { _, tag, message ->
        logs += "[$tag] $message"
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        logs.clear()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun logsFormattedRequestAndResponse_withCustomTag() {
        server.enqueue(
            MockResponse()
                .setBody("""{"code":0,"data":{"activated":true}}""")
                .addHeader("Content-Type", "application/json"),
        )

        val loggingConfig = HttpLoggingConfig(tag = "TestHttp")
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(FormattedHttpLoggingInterceptor(loggingConfig, logSink))
            .build()
        val retrofit = NetworkClientFactory.createRetrofit(
            config = networkConfig { baseUrl(server.url("/").toString()) },
            okHttpClient = client,
        )
        retrofit.create(RedeemApi::class.java)
            .redeem(RedeemRequest("ABC-123"))
            .execute()

        assertTrue(logs.any { it.contains("[TestHttp]") })
        assertTrue(logs.any { it.contains("HTTP REQUEST") })
        assertTrue(logs.any { it.contains("HTTP RESPONSE") })
        assertTrue(logs.any { it.contains("POST") })
        assertTrue(logs.any { it.contains("\"code\": 0") })
    }

    @Test
    fun skipsLogging_whenPathExcluded() {
        server.enqueue(
            MockResponse()
                .setBody("""{"code":0}""")
                .addHeader("Content-Type", "application/json"),
        )

        val loggingConfig = HttpLoggingConfig(
            tag = "Filtered",
            excludePathPatterns = listOf("/cards/redeem"),
        )
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(FormattedHttpLoggingInterceptor(loggingConfig, logSink))
            .build()
        val retrofit = NetworkClientFactory.createRetrofit(
            config = networkConfig { baseUrl(server.url("/").toString()) },
            okHttpClient = client,
        )
        retrofit.create(RedeemApi::class.java)
            .redeem(RedeemRequest("ABC"))
            .execute()

        assertTrue(logs.isEmpty())
    }
}

private interface RedeemApi {
    @POST("api/v1/activation/cards/redeem")
    fun redeem(@Body body: RedeemRequest): retrofit2.Call<RedeemResponse>
}

@Serializable
private data class RedeemRequest(val cardCode: String)

@Serializable
private data class RedeemResponse(val code: Int)
