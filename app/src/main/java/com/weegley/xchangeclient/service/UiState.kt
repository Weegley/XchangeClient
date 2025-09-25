package com.weegley.xchangeclient.service


data class UiState(
    val state: SessionState = SessionState.OFFLINE,
    val balance: String = "$0.00",
    val timeLeft: String = "--:--:--",
    val connectionType: String = "Unknown"
)
