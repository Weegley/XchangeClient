package com.weegley.xchangeclient.network

import com.weegley.xchangeclient.network.dto.ApiEnvelope
import com.weegley.xchangeclient.network.dto.ConnectStatus
import com.weegley.xchangeclient.network.dto.UserProfile
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // OAuth2 password grant – делается напрямую из AuthRepository через OkHttp, но оставим сигнатуру на всякий
    @FormUrlEncoded
    @POST("api/oauth/token")
    suspend fun loginOAuth(
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("remember") remember: Boolean = true
    ): Response<ResponseBody>

    // Профиль пользователя (как в фронтовом index-*.js: /api/user/{username})
    @GET("api/user/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): ApiEnvelope<UserProfile>

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
