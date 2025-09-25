package com.weegley.xchangeclient.network.dto

data class ConnectStatus(
    val status: String? = null,                 // SESSION_DATA_CONNECTED / ... / USER_NO_CREDIT
    val sessionId: Int? = null,
    val username: String? = null,
    val startDate: String? = null,
    val duration: Long? = null,
    val initialCredit: Double? = null,
    val remainingCredit: Double? = null,
    val remainingCreditCorpo: Double? = null,
    val remainingCreditPerso: Double? = null,
    val downloadSize: Long? = null,
    val uploadSize: Long? = null,
    val remainingDelayBeforeClose: Long? = null,
    val closeDate: String? = null,
    val closeDateCause: String? = null,
    val deviceName: String? = null,
)
