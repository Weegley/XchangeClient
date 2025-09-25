package com.weegley.xchangeclient.network

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

object BackendConfig {
    private const val BASE_URL = "https://m.xchange-box.com/"

    private val cookieManager by lazy {
        CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    }

    // Делаем клиент публичным (используется и Retrofit, и WsClient)
    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // Для отладки удобно BASIC/HEADERS; BODY оставь по необходимости
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .cookieJar(JavaNetCookieJar(cookieManager))
            // Общие заголовки и Bearer
            .addInterceptor { chain ->
                val orig = chain.request()
                val b = orig.newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "XchangeClient/1.0 (Android)")

                // Подставляем Bearer, если токен есть и если это НЕ oauth/token
                val urlStr = orig.url.toString()
                if (!urlStr.contains("/api/oauth/token")) {
                    TokenStore.accessToken?.let { token ->
                        // Не затираем, если явно уже задан где-то сверху
                        if (orig.header("Authorization") == null) {
                            b.header("Authorization", "Bearer $token")
                        }
                    }
                }

                chain.proceed(b.build())
            }
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
