package com.weegley.xchangeclient.network

import android.util.Log
import com.weegley.xchangeclient.network.dto.AuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {
    private const val TAG = "AuthRepository"

    /**
     * Логин через мобильный эндпоинт.
     * Возвращаем Result<Unit> — внутри обязательно возвращаем Unit.
     */
    suspend fun login(username: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val env = BackendConfig.api.loginAuth(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )
                val token: AuthToken = env.value ?: error("Empty token payload")
                val bearer = token.accessToken ?: error("accessToken is null/empty")

                TokenStore.setToken(bearer)
                Log.i(TAG, "login(): token received, expiresIn=${token.expiresIn}")

                Unit // ← ОБЯЗАТЕЛЬНО, чтобы тип был Result<Unit>
            }
        }

    fun logout() {
        TokenStore.clear()
    }
}
