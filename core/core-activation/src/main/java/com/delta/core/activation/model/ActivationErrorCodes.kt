package com.delta.core.activation.model

/**
 * Backend activation business error codes (delta-api ResultCode 2000+).
 */
object ActivationErrorCodes {
    const val CARD_INVALID_OR_USED = 2002
    const val CARD_CODE_REQUIRED = 2003
    const val CLIENT_CONTEXT_INVALID = 2004
    const val ACTIVATION_NOT_ACTIVE = 2007
    const val CARD_PUBLISHER_MISMATCH = 2012
    const val CARD_PUBLISHER_LOCKED = 2013
    const val CARD_TYPE_MISMATCH = 2018
    const val APK_PACKAGE_REQUIRED = 2022
    const val APK_PACKAGE_NOT_ALLOWED = 2023
    const val CARD_PACKAGE_MISMATCH = 2024
    const val TASK_PROGRESS_INVALID = 2010
    const val STEP_CARD_NOT_ENABLED = 2019
    const val STEP_CARD_MISMATCH = 2020
    const val STEP_ALREADY_UNLOCKED = 2021
    const val RATE_LIMIT_EXCEEDED = 1004

    fun messageFor(code: Int, fallback: String): String = when (code) {
        CARD_INVALID_OR_USED -> "卡密无效或已被使用"
        CARD_CODE_REQUIRED -> "卡密不能为空"
        CLIENT_CONTEXT_INVALID -> "客户端信息无效"
        ACTIVATION_NOT_ACTIVE -> "设备尚未激活"
        CARD_PUBLISHER_MISMATCH -> "卡密与当前入口不匹配"
        CARD_PUBLISHER_LOCKED -> "设备已绑定其他 Publisher"
        CARD_TYPE_MISMATCH -> "卡密类型不匹配"
        APK_PACKAGE_REQUIRED -> "缺少应用包名"
        APK_PACKAGE_NOT_ALLOWED -> "应用包名不可用"
        CARD_PACKAGE_MISMATCH -> "卡密与应用包名不匹配"
        TASK_PROGRESS_INVALID -> "任务进度无效"
        STEP_CARD_NOT_ENABLED -> "当前步骤未开启卡密解锁"
        STEP_CARD_MISMATCH -> "该卡密不属于当前步骤"
        STEP_ALREADY_UNLOCKED -> "当前步骤已解锁，无需重复操作"
        RATE_LIMIT_EXCEEDED -> "请求过于频繁，请稍后再试"
        else -> fallback
    }
}
