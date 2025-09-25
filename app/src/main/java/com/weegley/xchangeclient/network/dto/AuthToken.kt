package com.weegley.xchangeclient.network.dto

import com.google.gson.annotations.SerializedName

data class OAuthToken(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Long?,
    val refresh_token: String?
)
