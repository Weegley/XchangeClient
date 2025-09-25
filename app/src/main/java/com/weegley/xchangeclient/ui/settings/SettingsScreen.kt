@file:OptIn(ExperimentalMaterial3Api::class)

package com.weegley.xchangeclient.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weegley.xchangeclient.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val current by vm.state.collectAsStateWithLifecycle()

    var username by remember(current.username) { mutableStateOf(current.username) }
    var password by remember(current.password) { mutableStateOf(current.password) }
    var autoLogin by remember(current.autoLogin) { mutableStateOf(current.autoLogin) }
    var autoConnect by remember(current.autoConnect) { mutableStateOf(current.autoConnect) }
    var pwVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Settings") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(
                            imageVector = if (pwVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (pwVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Auto (re)login
            ListItem(
                headlineContent = { Text("Auto (re)login") },
                supportingContent = { Text("Login at app start / relogin on failure") },
                trailingContent = {
                    Switch(checked = autoLogin, onCheckedChange = { autoLogin = it })
                }
            )

            // Auto (re)start
            ListItem(
                headlineContent = { Text("Auto (re)start") },
                supportingContent = { Text("Start/Restart session automatically") },
                trailingContent = {
                    Switch(checked = autoConnect, onCheckedChange = { autoConnect = it })
                }
            )

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        vm.save(username, password, autoLogin, autoConnect)
                        onSave()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }

            Text(
                text = "Your settings are stored locally via DataStore.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
