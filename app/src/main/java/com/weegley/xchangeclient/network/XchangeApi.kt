package com.weegley.xchangeclient.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface XchangeApi {
    // TODO: проверь реальный путь/имена полей из HAR (временно — placeholders)
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<String>

    // Подключение сессии
    @POST("api/session/connect")
    suspend fun connect(): Response<String>

    // Отключение сессии
    @POST("api/session/disconnect")
    suspend fun disconnect(): Response<String>
}
