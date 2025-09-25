package com.weegley.xchangeclient.service

import android.util.Log
import com.weegley.xchangeclient.network.ApiService
import com.weegley.xchangeclient.network.dto.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Репозиторий для работы с данными пользователя (профиль, channelId и т.п.)
 */
class SessionRepository(
    private val api: ApiService
) {
    companion object {
        private const val TAG = "SessionRepository"
    }

    /**
     * Получить профиль пользователя по username.
     */
    suspend fun fetchUserProfile(username: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        runCatching {
            val env = api.getUser(username)
            Log.d(TAG, "fetchUserProfile($username): label=${env.label} desc=${env.successDescription}")
            env.value ?: error("Empty profile payload")
        }
    }

    /**
     * Достать первый DATA-канал из профиля пользователя.
     */
    suspend fun fetchFirstDataChannelId(username: String): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val env = api.getUser(username)
            Log.d(TAG, "fetchFirstDataChannelId($username): label=${env.label} desc=${env.successDescription}")
            val profile = env.value ?: error("Empty profile payload")

            val chId = profile.group
                ?.channels
                ?.firstOrNull { ch -> ch.trafficType.equals("DATA", ignoreCase = true) }
                ?.id
                ?: error("No DATA channel in profile")

            chId
        }
    }
}
