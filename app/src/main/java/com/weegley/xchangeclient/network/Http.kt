package com.weegley.xchangeclient.network

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeUnit

private const val TAG = "Http"

class InMemoryCookieJar : CookieJar {
    private val store = HashMap<String, MutableList<Cookie>>() // per-host

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val key = url.host
        val list = store.getOrPut(key) { mutableListOf() }
        list.removeAll { c -> cookies.any { it.name == c.name } }
        list.addAll(cookies)
        Log.d(TAG, "Saved cookies for $key: ${cookies.map { it.name }}")
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val list = store[url.host]?.filter { !it.hasExpired() } ?: emptyList()
        if (list.isNotEmpty()) {
            Log.d(TAG, "Load cookies for ${url.host}: ${list.map { it.name }}")
        }
        return list
    }

    private fun Cookie.hasExpired(): Boolean {
        return expiresAt < System.currentTimeMillis()
    }
}

object Http {
    // База: все запросы к xchange-box.com
    const val BASE = "https://xchange-box.com"

    // Общий JSON
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Один OkHttp на всё приложение
    val cookieJar: InMemoryCookieJar = InMemoryCookieJar()

    val client: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor { m -> Log.d(TAG, m) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }

        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "XchangeClient/1.0 (Android)")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(log)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}

// ===== простые DTO под /status =====
@kotlinx.serialization.Serializable
data class StatusResponse(
    val status: String? = null,
    val username: String? = null,
    val sessionId: Long? = null,
    val startDate: String? = null,
    val duration: Long? = null,
    val initialCredit: Double? = null,
    val remainingCredit: Double? = null,
    val remainingCreditCorpo: Double? = null,
    val deviceName: String? = null,
    val closeDate: String? = null,
    val closeDateCause: String? = null
)
