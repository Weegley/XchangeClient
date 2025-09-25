package com.weegley.xchangeclient.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object SessionBus {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    /** Установить новое состояние целиком */
    fun emit(ui: UiState) {
        _uiState.value = ui
    }

    /** Обновить через лямбда */
    fun update(block: (UiState) -> UiState) {
        _uiState.update(block)
    }
}
