package com.delta.core.network.model

class ApiBusinessException(
    val code: Int,
    override val message: String,
) : Exception(message)

class ApiNetworkException(
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause)
