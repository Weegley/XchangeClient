package com.weegley.xchangeclient.network

import android.util.Log
import com.weegley.xchangeclient.network.dto.ConnectStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ConnectRepository {
    private const val TAG = "ConnectRepository"

    suspend fun getStatus(): Result<ConnectStatus?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val env = BackendConfig.api.getConnectStatus()
                Log.d(TAG, "getStatus(): label=${env.label} message=${env.successDescription}")
                env.value
            }
        }

    suspend fun start(channelId: Int): Result<ConnectStatus?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val env = BackendConfig.api.startData(channelId)
                Log.i(TAG, "start(): label=${env.label} message=${env.successDescription}")
                env.value
            }
        }

    suspend fun stop(): Result<ConnectStatus?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val env = BackendConfig.api.stopData()
                Log.i(TAG, "stop(): label=${env.label} message=${env.successDescription}")
                env.value
            }
        }
}
