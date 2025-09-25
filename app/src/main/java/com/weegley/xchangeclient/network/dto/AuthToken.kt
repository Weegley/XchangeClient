package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Тело токена, которое приходит внутри ApiEnvelope.value
 * от POST /api/auth/login на m.xchange-box.com
 */
data class AuthToken(
    @SerializedName("accessToken")
    val accessToken: String?,

    @SerializedName("tokenType")
    val tokenType: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Long? = null
)
