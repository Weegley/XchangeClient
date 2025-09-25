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

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .cookieJar(JavaNetCookieJar(cookieManager))
            // Общий accept/user-agent
            .addInterceptor { chain ->
                val req = chain.request()
                val newReq = req.newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:143.0) Gecko/20100101 Firefox/143.0")
                    .build()
                chain.proceed(newReq)
            }
            // ВАЖНО: для /api/oauth/token добавляем Origin/Referer (как в браузере)
            .addInterceptor { chain ->
                val req = chain.request()
                val p = req.url.encodedPath
                val needsWebLikeHeaders = p == "/api/oauth/token"
                val newReq = if (needsWebLikeHeaders) {
                    req.newBuilder()
                        .header("Origin", "https://m.xchange-box.com")
                        .header("Referer", "https://m.xchange-box.com/")
                        .build()
                } else req
                chain.proceed(newReq)
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
