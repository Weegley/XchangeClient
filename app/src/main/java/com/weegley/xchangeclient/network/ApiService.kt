package com.weegley.xchangeclient.network

import com.weegley.xchangeclient.network.dto.ApiEnvelope
import com.weegley.xchangeclient.network.dto.AuthToken
import com.weegley.xchangeclient.network.dto.ConnectStatus
import com.weegley.xchangeclient.network.dto.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API для мобильного домена https://m.xchange-box.com/
 */
interface ApiService {

    // --- AUTH ---
    // Логин через JSON. Возвращает accessToken, который кладём в TokenStore.
    // POST https://m.xchange-box.com/api/auth/login
    @POST("api/auth/login")
    suspend fun loginAuth(
        @Body body: Map<String, String> // {"username": "...", "password": "..."}
    ): ApiEnvelope<AuthToken>

    // --- USER PROFILE ---
    // GET https://m.xchange-box.com/api/user/{username}
    // В .value приходит профиль пользователя (UserProfile)
    @GET("api/user/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): ApiEnvelope<UserProfile>

    // --- DATA CONNECTION STATUS ---
    // GET https://m.xchange-box.com/api/connection/DATA/status
    // В .value — текущее состояние data-сессии
    @GET("api/connection/DATA/status")
    suspend fun getConnectStatus(): ApiEnvelope<ConnectStatus?>

    // --- START DATA SESSION ---
    // POST https://m.xchange-box.com/api/connection/DATA/start?channelId=...
    // В .value — актуальный ConnectStatus (или null, но обычно приходит объект)
    @POST("api/connection/DATA/start")
    suspend fun startData(
        @Query("channelId") channelId: Int
    ): ApiEnvelope<ConnectStatus?>

    // --- STOP DATA SESSION ---
    // POST https://m.xchange-box.com/api/connection/DATA/stop
    // В .value — актуальный ConnectStatus (или null)
    @POST("api/connection/DATA/stop")
    suspend fun stopData(): ApiEnvelope<ConnectStatus?>
}
