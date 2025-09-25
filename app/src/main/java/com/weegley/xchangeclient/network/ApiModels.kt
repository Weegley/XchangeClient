package com.weegley.xchangeclient.network

import com.google.gson.annotations.SerializedName

/** Статус data-сессии */
data class ConnectStatus(
    val status: String?,
    val username: String?,
    val sessionId: Long?,
    val startDate: String?,
    val duration: Long?,
    val initialCredit: Double?,
    val remainingCredit: Double?,
    val remainingCreditCorpo: Double?,
    val remainingCreditPerso: Double?,
    val downloadSize: Long?,
    val uploadSize: Long?,
    val remainingDelayBeforeClose: Long?, // секунды
    val closeDate: String?,
    val closeDateCause: String?,
    val todayConnectionTime: Long?,
    val accountingMode: String?,
    val deviceName: String?
)

/** Ответ на старт */
data class StartResponse(
    val status: String?,         // "OK" / "KO"
    val hardwareId: String?,
    val username: String?,
    val sessionId: Long?,
    val startDate: String?,
    val initialCredit: Double?,
    val remainingCredit: Double?,
    val remainingCreditCorpo: Double?,
    val remainingCreditPerso: Double?,
    val closeDate: String?,
    val closeDateCause: String?,
    val accountingMode: String?,
    val deviceName: String?
)
