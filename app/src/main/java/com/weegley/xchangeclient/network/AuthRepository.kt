package com.weegley.xchangeclient.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

object AuthRepository {
    private const val TAG = "AuthRepository"
    private const val TOKEN_URL = "https://m.xchange-box.com/api/oauth/token"

    /**
     * Логин по OAuth2 password grant:
     * - вытаскиваем CLIENT_AUTH_TOKEN из фронтовых JS
     * - POST x-www-form-urlencoded на /api/oauth/token
     * - сохраняем access_token в TokenStore
     */
    suspend fun login(username: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val client = BackendConfig.okHttpClient

                // достаём токен; ВАЖНО — теперь возвращается именно base64, а не весь скрипт
                val clientToken = ClientAuthProvider.fetchClientAuthToken(client)
                    ?: error("CLIENT_AUTH_TOKEN not found")

                // формируем x-www-form-urlencoded
                val form = buildString {
                    append("grant_type=password")
                    append("&username=")
                    append(URLEncoder.encode(username, "UTF-8"))
                    append("&password=")
                    append(URLEncoder.encode(password, "UTF-8"))
                    append("&remember=true")
                }
                val body = form.toRequestBody("application/x-www-form-urlencoded".toMediaType())

                val req = Request.Builder()
                    .url(TOKEN_URL)
                    .header("Authorization", "Basic $clientToken")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Origin", "https://m.xchange-box.com")
                    .header("Referer", "https://m.xchange-box.com/")
                    .post(body)
                    .build()

                Log.i(TAG, "--> POST $TOKEN_URL")
                client.newCall(req).execute().use { resp ->
                    val code = resp.code
                    val bodyStr = resp.body?.string().orEmpty()
                    Log.i(TAG, "<-- $code $TOKEN_URL")

                    if (!resp.isSuccessful) {
                        Log.e(TAG, "login() failed $code: $bodyStr")
                        error("HTTP $code")
                    }

                    // ожидаем JSON вида { access_token, refresh_token, expires_in, ... }
                    val access = Regex(""""access_token"\s*:\s*"([^"]+)"""")
                        .find(bodyStr)?.groupValues?.getOrNull(1)
                    require(!access.isNullOrBlank()) { "Empty access_token" }

                    TokenStore.setToken(access)
                    Log.i(TAG, "login(): access_token received")
                }

                Unit
            }
        }

    fun logout() {
        TokenStore.clear()
    }
}
