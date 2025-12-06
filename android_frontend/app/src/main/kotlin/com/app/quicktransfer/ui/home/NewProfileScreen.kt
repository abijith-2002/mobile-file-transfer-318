package com.app.quicktransfer.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * A screen for creating a new connection profile. It includes Material 3 input fields
 * for Profile Name, Host Username, Host IP, Port, and Password, all styled with 16dp
 * rounded corners and a 2dp border, and an Elevated Save button centered horizontally.
 *
 * Parameters:
 * - onSave: Callback invoked when user presses Save. Provides the entered values:
 *           name, username, host, port, password.
 * - modifier: Optional modifier for this screen.
 * - onBack: Callback invoked when the user taps the back navigation icon in the top app bar.
 *
 * Returns:
 * - None. Renders the UI.
 */
 // PUBLIC_INTERFACE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProfileScreen(
    onSave: (name: String, username: String, host: String, port: Int, password: String) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    // Local state as no ViewModel/state hoisting is present for this screen yet
    var profileName by remember { mutableStateOf("") }
    var hostUsername by remember { mutableStateOf("") }
    var hostIp by remember { mutableStateOf("") }
    var portText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val shape = RoundedCornerShape(16.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create new profile",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Profile Name
            BorderedTextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = "Profile Name",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Host Username
            BorderedTextField(
                value = hostUsername,
                onValueChange = { hostUsername = it },
                label = "Host Username",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Host IP (allow decimal keyboard for dot entry)
            BorderedTextField(
                value = hostIp,
                onValueChange = { hostIp = it },
                label = "Host IP",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Port (numeric)
            BorderedTextField(
                value = portText,
                onValueChange = { text ->
                    // Accept only digits
                    if (text.all { it.isDigit() }) {
                        portText = text
                    }
                },
                label = "Port",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Password (mask with toggle)
            BorderedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedButton(
                onClick = {
                    val port = portText.toIntOrNull() ?: 22
                    onSave(profileName.trim(), hostUsername.trim(), hostIp.trim(), port, password)
                },
                elevation = ButtonDefaults.elevatedButtonElevation(),
                enabled = profileName.isNotBlank() &&
                    hostUsername.isNotBlank() &&
                    hostIp.isNotBlank() &&
                    portText.isNotBlank() &&
                    password.isNotBlank(),
                // Center horizontally, keep wrap-content width
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Save")
            }
        }
    }
}

/**
 * A Material 3 TextField wrapped with a 2dp border and 16dp rounded corners.
 */
@Composable
private fun BorderedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val borderColor = MaterialTheme.colorScheme.outline
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .border(width = 2.dp, color = borderColor, shape = shape)
            .clip(shape),
        label = { Text(text = label) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        shape = shape,
        colors = TextFieldDefaults.colors(
            // Keep filled container consistent with theme
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            // Keep the visible stroke consistent by hiding the built-in indicator
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

/**
 * A Material 3 Password TextField with visibility toggle, wrapped with 2dp border and 16dp corners.
 */
@Composable
private fun BorderedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val visualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .border(width = 2.dp, color = borderColor, shape = shape)
            .clip(shape),
        label = { Text(text = label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (passwordVisible) "Hide password" else "Show password"
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            // Hide the built-in indicator line to present a consistent 2dp stroke
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
