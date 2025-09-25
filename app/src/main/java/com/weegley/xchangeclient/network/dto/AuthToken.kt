package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("token_type")  val tokenType: String? = null,
    @SerializedName("expires_in")  val expiresIn: Long? = null
)
