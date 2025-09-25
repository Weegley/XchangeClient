package com.weegley.xchangeclient.network

import android.util.Log
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

object WsClient {

    private const val TAG = "WsClient"
    // при необходимости поменяем путь на реальный (например, "/ws" или другой)
    private const val WS_URL = "wss://m.xchange-box.com/ws"

    private var socket: WebSocket? = null

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
            Log.i(TAG, "WS onOpen: $response")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "WS onMessage: $text")
            // TODO: парсинг сообщений DEVICES / CONNECT_STATUS / NOTIFICATION
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "WS onMessage bytes: ${bytes.size}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "WS onClosing: $code $reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "WS onClosed: $code $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            Log.e(TAG, "WS onFailure: ${t.message}", t)
        }
    }

    fun isConnected(): Boolean = socket != null

    fun connect() {
        if (socket != null) return
        val client = BackendConfig.okHttpClient
        val req = Request.Builder().url(WS_URL).build()
        socket = client.newWebSocket(req, listener)
        Log.i(TAG, "WS connect() requested")
    }

    fun disconnect() {
        socket?.close(1000, "bye")
        socket = null
        Log.i(TAG, "WS disconnect()")
    }
}
