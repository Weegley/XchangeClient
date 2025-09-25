package com.weegley.xchangeclient.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

object BackendConfig {
    private const val TAG = "BackendConfig"
    private const val BASE_URL = "https://m.xchange-box.com/"

    private val cookieManager by lazy {
        CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ALL) }
    }

    private val authInterceptor = Interceptor { chain ->
        val req = chain.request()
        val path = req.url.encodedPath
        // НЕ прикручиваем Bearer к запросу получения токена
        if (path.startsWith("/api/oauth/token")) {
            return@Interceptor chain.proceed(req)
        }
        val token = TokenStore.accessToken
        if (!token.isNullOrBlank()) {
            val newReq = req.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newReq)
        } else {
            chain.proceed(req)
        }
    }

    private val logging = HttpLoggingInterceptor { msg ->
        Log.i("okhttp.OkHttpClient", msg)
    }.apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    // Делаем клиент доступным и для Retrofit, и для WsClient
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                val base = chain.request().newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "XchangeClient/1.0 (Android)")
                    .build()
                chain.proceed(base)
            }
            .addInterceptor(authInterceptor)
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
