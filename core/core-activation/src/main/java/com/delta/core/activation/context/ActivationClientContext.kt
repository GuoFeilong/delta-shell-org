package com.delta.core.activation.context

/**
 * Static client metadata supplied by each app module (channel, publisher, package name, etc.).
 */
interface ActivationClientContext {
    val clientOs: String
    val clientChannel: String
    val clientVersionCode: Int?
    val clientVersionName: String?
        get() = null
    val publisherKey: String?
    val appPackageName: String
}
