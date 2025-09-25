package com.weegley.xchangeclient.network

/**
 * Простое in-memory хранилище Bearer-токена.
 * При желании позже перенесём в DataStore.
 */
object TokenStore {
    @Volatile
    var accessToken: String? = null
        private set

    fun setToken(token: String?) {
        accessToken = token
    }

    fun clear() {
        accessToken = null
    }
}
