package com.weegley.xchangeclient.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weegley.xchangeclient.data.SettingsData
import com.weegley.xchangeclient.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    // ВАЖНО: SettingsRepository.init(context) должен быть вызван один раз (например, в Application.onCreate или MainActivity.onCreate)
    val state: StateFlow<SettingsData> =
        SettingsRepository.settings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsData()
        )

    fun save(username: String, password: String, autoLogin: Boolean, autoConnect: Boolean) {
        viewModelScope.launch {
            SettingsRepository.save { cur ->
                cur.copy(
                    username = username,
                    password = password,
                    autoLogin = autoLogin,
                    autoConnect = autoConnect
                )
            }
        }
    }
}
