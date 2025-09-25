package com.weegley.xchangeclient.network

/**
 * Простейшее in-memory хранилище Bearer-токена.
 * При желании можно сохранить в DataStore (не обязательно).
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
