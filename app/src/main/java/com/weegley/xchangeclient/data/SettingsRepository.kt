package com.weegley.xchangeclient.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "settings"

private val Context.ds by preferencesDataStore(DS_NAME)

data class SettingsData(
    val username: String = "",
    val password: String = "",
    val autoLogin: Boolean = false,
    val autoConnect: Boolean = false
)

object SettingsRepository {
    private lateinit var appContext: Context

    private val KEY_USERNAME = stringPreferencesKey("username")
    private val KEY_PASSWORD = stringPreferencesKey("password")
    private val KEY_AUTO_LOGIN = booleanPreferencesKey("auto_login")
    private val KEY_AUTO_CONNECT = booleanPreferencesKey("auto_connect")

    fun init(ctx: Context) {
        appContext = ctx.applicationContext
    }

    val settings: Flow<SettingsData> by lazy {
        appContext.ds.data.map { p ->
            SettingsData(
                username = p[KEY_USERNAME] ?: "",
                password = p[KEY_PASSWORD] ?: "",
                autoLogin = p[KEY_AUTO_LOGIN] ?: false,
                autoConnect = p[KEY_AUTO_CONNECT] ?: false
            )
        }
    }

    suspend fun save(block: (SettingsData) -> SettingsData) {
        appContext.ds.edit { p ->
            val cur = SettingsData(
                username = p[KEY_USERNAME] ?: "",
                password = p[KEY_PASSWORD] ?: "",
                autoLogin = p[KEY_AUTO_LOGIN] ?: false,
                autoConnect = p[KEY_AUTO_CONNECT] ?: false
            )
            val n = block(cur)
            p[KEY_USERNAME] = n.username
            p[KEY_PASSWORD] = n.password
            p[KEY_AUTO_LOGIN] = n.autoLogin
            p[KEY_AUTO_CONNECT] = n.autoConnect
        }
    }
}
