package com.weegley.xchangeclient.service

import android.util.Log
import com.weegley.xchangeclient.network.ApiService
import com.weegley.xchangeclient.network.dto.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRepository(
    private val api: ApiService
) {
    companion object { private const val TAG = "SessionRepository" }

    suspend fun fetchUserProfile(username: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        runCatching {
            val env = api.getUserProfile(username)
            Log.d(TAG, "fetchUserProfile($username): label=${env.label} desc=${env.successDescription}")
            env.value ?: error("Empty profile payload")
        }
    }

    suspend fun fetchFirstDataChannelId(username: String): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val env = api.getUserProfile(username)
            Log.d(TAG, "fetchFirstDataChannelId($username): label=${env.label} desc=${env.successDescription}")
            val profile = env.value ?: error("Empty profile payload")

            val chId = profile.group
                ?.channels
                ?.firstOrNull { it.trafficType.equals("DATA", ignoreCase = true) }
                ?.id ?: error("No DATA channel in profile")
            chId
        }
    }
}
