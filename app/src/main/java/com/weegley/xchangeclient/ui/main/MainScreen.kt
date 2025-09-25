package com.weegley.xchangeclient.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weegley.xchangeclient.R
import com.weegley.xchangeclient.service.SessionBus
import com.weegley.xchangeclient.service.SessionService
import com.weegley.xchangeclient.service.ServiceActions
import com.weegley.xchangeclient.service.SessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenSettings: () -> Unit
) {
    val ui by SessionBus.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current.applicationContext

    val statusText = when (ui.state) {
        SessionState.CONNECTED -> R.string.status_connected
        SessionState.LOGGED_IN -> R.string.status_logged_in
        SessionState.OFFLINE   -> R.string.status_logged_out
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_dashboard)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // STATUS
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.label_status),
                        style = MaterialTheme.typography.titleMedium
                    )
                    AssistChip(
                        onClick = { /* no-op */ },
                        label = { Text(stringResource(statusText)) }
                    )
                }
            }

            // BALANCE / TIME LEFT
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoTile(
                    title = stringResource(R.string.label_balance),
                    value = ui.balance,
                    modifier = Modifier.weight(1f)
                )
                InfoTile(
                    title = stringResource(R.string.label_time_left),
                    value = ui.timeLeft,
                    modifier = Modifier.weight(1f)
                )
            }

            // CONNECTION TYPE
            InfoTile(
                title = stringResource(R.string.label_connection_type),
                value = ui.connectionType,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            // ACTIONS
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // LOGIN
                Button(
                    onClick = {
                        SessionService.start(ctx)
                        ctx.startService(
                            Intent(ctx, SessionService::class.java)
                                .setAction(ServiceActions.ACTION_LOGIN)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_login))
                }

                // CONNECT / DISCONNECT — текст зависит от ui.state
                val isConnected = ui.state == SessionState.CONNECTED
                OutlinedButton(
                    onClick = {
                        SessionService.start(ctx)
                        val action = if (isConnected)
                            ServiceActions.ACTION_DISCONNECT
                        else
                            ServiceActions.ACTION_CONNECT
                        ctx.startService(
                            Intent(ctx, SessionService::class.java).setAction(action)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isConnected) stringResource(R.string.action_disconnect)
                        else stringResource(R.string.action_connect)
                    )
                }

                Text(
                    text = "(* UI bound to service state; backend later *)",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun InfoTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
