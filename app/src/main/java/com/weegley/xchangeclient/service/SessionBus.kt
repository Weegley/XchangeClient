package com.weegley.xchangeclient.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionBus {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun update(block: (UiState) -> UiState) {
        _uiState.value = block(_uiState.value)
    }
}
