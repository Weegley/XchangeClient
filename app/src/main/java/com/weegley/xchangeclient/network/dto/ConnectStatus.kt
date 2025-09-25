package com.weegley.xchangeclient.network.dto

/**
 * Минимальный DTO статуса подключения.
 * При необходимости добавим поля из твоих WS-сообщений.
 */
data class ConnectStatus(
    val status: String? = null,
    val username: String? = null,
    val sessionId: Long? = null,
    val startDate: String? = null,
    val duration: Long? = null,
    val remainingCredit: Double? = null,
    val deviceName: String? = null,
    val closeDate: String? = null
)
