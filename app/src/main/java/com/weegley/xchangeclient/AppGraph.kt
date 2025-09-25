package com.weegley.xchangeclient

import android.app.Application
import com.weegley.xchangeclient.data.SettingsRepository

object AppGraph {
    fun init(app: Application) {
        // Инициализация хранилища настроек (DataStore)
        SettingsRepository.init(app)
        // BackendConfig.init(app) не нужен — всё ленивое
    }
}
