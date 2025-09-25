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

    private val okHttp: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // Поменяй на BODY при отладке, если надо видеть JSON
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor { chain ->
                val b = chain.request().newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "XchangeClient/1.0 (Android)")
                // Если есть токен — подложим Bearer
                TokenStore.accessToken?.let { b.header("Authorization", "Bearer $it") }
                chain.proceed(b.build())
            }
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /** Делаю публичным для WsClient */
    val okHttpClient: OkHttpClient
        get() = okHttp

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
