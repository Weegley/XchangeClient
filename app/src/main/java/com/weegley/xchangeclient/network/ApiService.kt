package com.weegley.xchangeclient.network

import com.weegley.xchangeclient.network.dto.OAuthToken
import com.weegley.xchangeclient.network.dto.ApiEnvelope
import com.weegley.xchangeclient.network.dto.ConnectStatus
import com.weegley.xchangeclient.network.dto.UserProfile
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // OAuth password grant (мобильный сайт)
    @FormUrlEncoded
    @POST("api/oauth/token")
    suspend fun loginOAuthPassword(
        @Header("Authorization") authHeaderBasic: String,   // "Basic <CLIENT_AUTH_TOKEN>"
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String,
        // не обязателен, но веб иногда шлёт
        @Field("remember") remember: Boolean = true
    ): OAuthToken

    // Профиль пользователя
    @GET("api/user/{username}")
    suspend fun getUserProfile(@Path("username") username: String): ApiEnvelope<UserProfile>

    // Статус data-сессии
    @GET("api/connection/DATA/status")
    suspend fun getConnectStatus(): ApiEnvelope<ConnectStatus>

    // Старт data-сессии
    @POST("api/connection/DATA/start")
    suspend fun startData(@Query("channelId") channelId: Int): ApiEnvelope<ConnectStatus?>

    // Стоп data-сессии
    @POST("api/connection/DATA/stop")
    suspend fun stopData(): ApiEnvelope<ConnectStatus?>
}
