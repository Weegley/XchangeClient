package com.weegley.xchangeclient

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.weegley.xchangeclient.notifications.Notifier
import com.weegley.xchangeclient.navigation.AppNav
import com.weegley.xchangeclient.service.SessionService
import com.weegley.xchangeclient.ui.theme.AppTheme
import com.weegley.xchangeclient.data.SettingsRepository

class MainActivity : ComponentActivity() {

    private val reqPostNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Notifier.ensureChannel(this)
            SessionService.start(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppGraph.init(application)
        super.onCreate(savedInstanceState)

        // ВАЖНО: инициализация DataStore-репозитория
        SettingsRepository.init(applicationContext)

        Notifier.ensureChannel(this)

        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                SessionService.start(this)
            } else {
                reqPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            SessionService.start(this)
        }

        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // ВАЖНО: только AppNav(), никаких прямых вызовов MainScreen тут
                    AppNav()
                }
            }
        }
    }
}
