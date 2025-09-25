package com.weegley.xchangeclient.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.weegley.xchangeclient.data.SettingsRepository
import com.weegley.xchangeclient.network.AuthRepository
import com.weegley.xchangeclient.network.BackendConfig
import com.weegley.xchangeclient.notifications.Notifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SessionService : Service() {

    companion object {
        private const val TAG = "SessionService"

        fun start(ctx: Context) {
            val intent = Intent(ctx, SessionService::class.java)
                .setAction(ServiceActions.ACTION_START)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(ctx, intent)
            } else {
                ctx.startService(intent)
            }
        }

        fun stop(ctx: Context) {
            ctx.stopService(Intent(ctx, SessionService::class.java))
        }
    }

    // --- deps / scope ---
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val api by lazy { BackendConfig.api }
    private val repo by lazy { SessionRepository(api) }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
        Notifier.ensureChannel(this)
        // поднимем FGS сразу (покажем текущее состояние — при старте OFFLINE)
        startForegroundWithUi(SessionBus.uiState.value)
    }

    private fun startForegroundWithUi(ui: UiState) {
        val notification: Notification = Notifier.build(this, ui)
        if (Build.VERSION.SDK_INT >= 34) {
            // Чётко указываем тип: DATA_SYNC (подходит для сетевого обмена/синхронизации)
            startForeground(
                Notifier.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(Notifier.NOTIFICATION_ID, notification)
        }
        Log.d(TAG, "startForeground()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.i(TAG, "onStartCommand action=$action")

        when (action) {
            ServiceActions.ACTION_START -> {
                // просто обновим уведомление актуальным UiState
                notifyCurrent()
            }

            ServiceActions.ACTION_LOGIN -> {
                scope.launch { doLoginAndPrefetch() }
            }

            ServiceActions.ACTION_CONNECT -> {
                scope.launch { doConnect() }
            }

            ServiceActions.ACTION_DISCONNECT -> {
                scope.launch { doDisconnect() }
            }
        }
        return START_STICKY
    }

    private fun notifyCurrent() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(Notifier.NOTIFICATION_ID, Notifier.build(this, SessionBus.uiState.value))
    }

    /**
     * Логин в мобильное API (OAuth) + подтянуть профиль (баланс и т.п.).
     */
    private suspend fun doLoginAndPrefetch() {
        runCatching {
            val s = SettingsRepository.settings.first()
            val username = s.username
            val password = s.password
            require(username.isNotBlank() && password.isNotBlank()) { "Empty credentials" }

            // 1) OAuth login → сохраняем Bearer в TokenStore
            AuthRepository.login(username, password).getOrThrow()

            // 2) профиль пользователя
            val profile = repo.fetchUserProfile(username).getOrThrow()
            val corp = profile.corporateCredit ?: 0.0
            val pers = profile.personalCredit ?: 0.0
            val total = corp + pers

            withContext(Dispatchers.Main) {
                SessionBus.update {
                    it.copy(
                        state = SessionState.LOGGED_IN,
                        balance = "$${"%.2f".format(total)}"
                    )
                }
                notifyCurrent()
            }
        }.onFailure { e ->
            Log.e(TAG, "Login/prefetch failed", e)
        }
    }

    /**
     * Старт DATA-сессии:
     * - выясняем channelId по профилю
     * - POST /api/connection/DATA/start?channelId=...
     */
    private suspend fun doConnect() {
        runCatching {
            val s = SettingsRepository.settings.first()
            val username = s.username
            require(username.isNotBlank()) { "Empty username" }

            val chId = repo.fetchFirstDataChannelId(username).getOrThrow()
            val env = api.startData(channelId = chId)
            Log.i(TAG, "startData(): label=${env.label} desc=${env.successDescription}")

            withContext(Dispatchers.Main) {
                SessionBus.update { it.copy(state = SessionState.CONNECTED) }
                notifyCurrent()
            }
        }.onFailure { e ->
            Log.e(TAG, "Connect failed", e)
        }
    }

    /**
     * Остановка DATA-сессии.
     */
    private suspend fun doDisconnect() {
        runCatching {
            val env = api.stopData()
            Log.i(TAG, "stopData(): label=${env.label} desc=${env.successDescription}")
            withContext(Dispatchers.Main) {
                SessionBus.update { it.copy(state = SessionState.LOGGED_IN) }
                notifyCurrent()
            }
        }.onFailure { e ->
            Log.e(TAG, "Disconnect failed", e)
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
