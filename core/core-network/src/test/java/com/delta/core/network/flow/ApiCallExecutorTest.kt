package com.delta.core.network.flow

import com.delta.core.network.model.ApiResponse
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ApiCallExecutorTest {
    private val executor = ApiCallExecutor()

    @Test
    fun asFlow_emitsLoadingThenSuccess() = runTest {
        val results = executor.asFlow { "ok" }.toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResult.Loading)
        assertEquals(ApiResult.Success("ok"), results[1])
    }

    @Test
    fun asFlow_emitsErrorOnIOException() = runTest {
        val results = executor.asFlow { throw IOException("offline") }.toList()

        assertTrue(results[1] is ApiResult.Error)
        val error = results[1] as ApiResult.Error
        assertEquals(-1, error.code)
        assertEquals("offline", error.message)
    }

    @Test
    fun asEnvelopeFlow_unwrapsSuccessData() = runTest {
        val results = executor.asEnvelopeFlow {
            ApiResponse(code = 0, message = "ok", data = 42)
        }.toList()

        assertEquals(ApiResult.Success(42), results[1])
    }

    @Test
    fun asEnvelopeFlow_emitsBusinessError() = runTest {
        val results = executor.asEnvelopeFlow {
            ApiResponse(code = 1001, message = "token expired", data = null)
        }.toList()

        val error = results[1] as ApiResult.Error
        assertEquals(1001, error.code)
        assertEquals("token expired", error.message)
    }
}
