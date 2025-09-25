package com.weegley.xchangeclient.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {
    private const val TAG = "AuthRepository"

    suspend fun login(username: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val token = BackendConfig.api.loginOAuth(username = username, password = password)
                val bearer = token.accessToken ?: error("Empty access_token")
                TokenStore.setToken(bearer)
                Log.i(TAG, "login(): token received, expires_in=${token.expiresIn}")
                Unit
            }
        }

    fun logout() {
        TokenStore.clear()
    }
}
