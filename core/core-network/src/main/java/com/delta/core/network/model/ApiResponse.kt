package com.delta.core.network.model

/**
 * Unified backend envelope. Adjust [SUCCESS_CODE] to match your API contract.
 */
@kotlinx.serialization.Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String = "",
    val data: T? = null,
) {
    val isSuccess: Boolean
        get() = code == SUCCESS_CODE

    companion object {
        const val SUCCESS_CODE = 0
    }
}
