package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Унифицированный ответ Xchange. Полезная нагрузка в [value].
 */
data class ApiEnvelope<T>(
    @SerializedName("return")
    val value: T? = null,

    @SerializedName("label")
    val label: String? = null,

    @SerializedName("label_parameters")
    val labelParameters: Any? = null,

    @SerializedName("success_description")
    val successDescription: String? = null,

    @SerializedName("additionalDetails")
    val additionalDetails: Any? = null,

    @SerializedName("pendingCreditUpdates")
    val pendingCreditUpdates: List<Any>? = null
)
