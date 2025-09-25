package com.weegley.xchangeclient.service

import android.util.Log
import org.json.JSONObject

// События от WS — сырые
sealed class WsEvent {
    data object Open : WsEvent()
    data class Text(val json: String) : WsEvent()
    data class Closed(val code: Int, val reason: String) : WsEvent()
    data class Failure(val error: Throwable) : WsEvent()
}

/**
 * Простейший парсер входящих сообщений (без зависимостей).
 * Ожидаемые типы: DEVICES, CONNECT_STATUS, NOTIFICATION
 */
object WsMappers {
    private const val TAG = "WsMappers"

    fun applyIncoming(json: String, prev: UiState): UiState {
        return try {
            val root = JSONObject(json)
            val type = root.optString("type")
            val data = root.optJSONObject("data")

            when (type) {
                "CONNECT_STATUS" -> mapConnectStatus(data, prev)
                "DEVICES" -> {
                    // Можно вытянуть картинку/тип модема для connectionType
                    val arr = data?.optJSONArray("data") ?: return prev
                    if (arr.length() > 0) {
                        val obj = arr.getJSONObject(0)
                        val name = obj.optString("name", prev.connectionType)
                        prev.copy(connectionType = name.ifBlank { prev.connectionType })
                    } else prev
                }
                "NOTIFICATION" -> {
                    val msg = data?.optString("message")
                    Log.w(TAG, "NOTIFICATION: $msg")
                    prev // UI можно дополнить тостом/баннером при желании
                }
                else -> prev
            }
        } catch (t: Throwable) {
            Log.e(TAG, "parse error: ${t.message}")
            prev
        }
    }

    private fun mapConnectStatus(data: JSONObject?, prev: UiState): UiState {
        if (data == null) return prev
        val status = data.optString("status")

        val state = when (status) {
            "SESSION_DATA_CONNECTED" -> SessionState.CONNECTED
            "SESSION_DATA_CONNECTING_1",
            "SESSION_DATA_CONNECTING_2",
            "SESSION_DATA_CONNECTING_3",
            "SESSION_DATA_CONNECTING_4" -> SessionState.LOGGED_IN
            "SESSION_DATA_DISCONNECTED",
            "USER_NO_CREDIT",
            "USER_DISABLED",
            "USER_BANNED" -> SessionState.OFFLINE
            else -> prev.state
        }

        // кредит
        val remainingCredit = data.optDouble("remainingCredit", -1.0)
        val balance = if (remainingCredit >= 0.0) {
            // форматируем примерно
            "$" + String.format("%.2f", remainingCredit)
        } else prev.balance

        // тип соединения
        val deviceName = data.optString("deviceName", "")
        val connectionType = if (deviceName.isNotBlank()) deviceName else prev.connectionType

        // оценка оставшегося времени до closeDate
        val timeLeft = prev.timeLeft // можно позже высчитывать из closeDate/start+duration

        return prev.copy(
            state = state,
            balance = balance,
            connectionType = connectionType,
            timeLeft = timeLeft
        )
    }
}
