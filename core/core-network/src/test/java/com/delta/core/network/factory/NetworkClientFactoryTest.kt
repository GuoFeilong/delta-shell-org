package com.delta.core.network.factory

import com.delta.core.network.config.networkConfig
import kotlinx.serialization.Serializable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.http.GET

class NetworkClientFactoryTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun createsRetrofitServiceWithCustomHeadersAndHost() {
        server.enqueue(MockResponse().setBody("""{"message":"ok"}""").addHeader("Content-Type", "application/json"))

        val config = networkConfig {
            baseUrl(server.url("/").toString())
            header("X-App-Id", "feature-home")
            hostHeader("api.example.com")
        }

        val api = NetworkClientFactory.createApiService<TestApi>(config)
        val response = api.ping().execute()

        assertEquals("ok", response.body()?.message)

        val recorded = server.takeRequest()
        assertEquals("feature-home", recorded.getHeader("X-App-Id"))
        assertEquals("api.example.com", recorded.getHeader("Host"))
        assertEquals("/ping", recorded.path)
    }
}

private interface TestApi {
    @GET("ping")
    fun ping(): Call<PingResponse>
}

@Serializable
private data class PingResponse(val message: String)
