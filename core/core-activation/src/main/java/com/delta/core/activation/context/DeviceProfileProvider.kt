package com.delta.core.activation.context

fun interface DeviceProfileProvider {
    fun snapshot(): DeviceProfileSnapshot
}
