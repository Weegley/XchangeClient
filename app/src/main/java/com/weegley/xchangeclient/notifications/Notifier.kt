package com.weegley.xchangeclient.notifications

import android.app.*
import android.content.*
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.weegley.xchangeclient.MainActivity
import com.weegley.xchangeclient.R
import com.weegley.xchangeclient.service.*

private const val TAG = "Notifier"

object Notifier {

    const val CHANNEL_ID = "session_status_channel"
    const val CHANNEL_NAME = "Session status"
    const val NOTIFICATION_ID = 1001

    private const val REQ_MAIN = 1
    private const val REQ_LOGIN = 2
    private const val REQ_CONNECT = 3
    private const val REQ_DISCONNECT = 4

    fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = mgr.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val ch = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows login/connection status"
                    setShowBadge(false)
                }
                mgr.createNotificationChannel(ch)
                Log.i(TAG, "created channel id=$CHANNEL_ID name=$CHANNEL_NAME")
            }
        }
    }

    fun build(
        ctx: Context,
        ui: UiState
    ): Notification {

        val title = when (ui.state) {
            SessionState.OFFLINE   -> "Offline"
            SessionState.LOGGED_IN -> "Logged in"
            SessionState.CONNECTED -> "Connected"
        }

        val content = when {
            ui.state == SessionState.CONNECTED && ui.timeLeft.isNotBlank() && ui.timeLeft != "--:--:--" ->
                "Time left: ${ui.timeLeft}"
            else -> "XChange Client"
        }

        val mainPi = PendingIntent.getActivity(
            ctx,
            REQ_MAIN,
            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag()
        )

        val loginPi = PendingIntent.getService(
            ctx, REQ_LOGIN,
            Intent(ctx, SessionService::class.java).setAction(ServiceActions.ACTION_LOGIN),
            PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag()
        )
        val connectPi = PendingIntent.getService(
            ctx, REQ_CONNECT,
            Intent(ctx, SessionService::class.java).setAction(ServiceActions.ACTION_CONNECT),
            PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag()
        )
        val disconnectPi = PendingIntent.getService(
            ctx, REQ_DISCONNECT,
            Intent(ctx, SessionService::class.java).setAction(ServiceActions.ACTION_DISCONNECT),
            PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag()
        )

        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(mainPi)

        when (ui.state) {
            SessionState.OFFLINE -> builder.addAction(0, "Login", loginPi)
            SessionState.LOGGED_IN -> builder.addAction(0, "Connect", connectPi)
            SessionState.CONNECTED -> builder.addAction(0, "Disconnect", disconnectPi)
        }

        return builder.build()
    }

    private fun immutableFlag(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
}
