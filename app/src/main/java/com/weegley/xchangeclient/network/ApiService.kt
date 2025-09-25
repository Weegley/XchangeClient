package com.weegley.xchangeclient.network

import com.weegley.xchangeclient.network.dto.ApiEnvelope
import com.weegley.xchangeclient.network.dto.AuthToken
import com.weegley.xchangeclient.network.dto.ConnectStatus
import com.weegley.xchangeclient.network.dto.UserProfile
import retrofit2.http.*

/**
 * Мобильное API m.xchange-box.com
 */
interface ApiService {

    // OAuth: возвращает "плоский" JSON с токеном, НЕ ApiEnvelope
    @FormUrlEncoded
    @POST("api/oauth/token")
    suspend fun loginOAuth(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String = "mobile"
    ): AuthToken

    // Профиль пользователя (обёрнут ApiEnvelope с полем "return")
    @GET("api/user/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): ApiEnvelope<UserProfile>

    // Статус DATA-сессии
    @GET("api/connection/DATA/status")
    suspend fun getConnectStatus(): ApiEnvelope<ConnectStatus>

    // Старт DATA-сессии
    @POST("api/connection/DATA/start")
    suspend fun startData(@Query("channelId") channelId: Int): ApiEnvelope<ConnectStatus>

    // Стоп DATA-сессии
    @POST("api/connection/DATA/stop")
    suspend fun stopData(): ApiEnvelope<ConnectStatus>
}
