package com.app.quicktransfer.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.app.quicktransfer.data.ProfileRepository
import kotlinx.coroutines.launch

/**
 * A screen for creating or editing a connection profile. It includes Material 3 input fields
 * for Profile Name, Host Username, Password, Host IP, and Port, all styled with rounded corners,
 * and an Elevated Save button centered horizontally.
 *
 * Behavior:
 * - Save is disabled if any field is empty.
 * - On Save:
 *   - If creating, persists a new profile via ProfileRepository and navigates back.
 *   - If editing (profileId provided), updates the existing profile (no duplicate) and navigates back.
 *
 * Parameters:
 * - repository: Profile repository used to persist the profile.
 * - profileId: Optional profile id; when provided, the screen initializes in edit mode.
 * - modifier: Optional modifier for this screen.
 * - onBack: Callback invoked when the user taps the back icon or after successful save.
 *
 * Returns:
 * - None. Renders the UI.
 */
// PUBLIC_INTERFACE
@Composable
fun NewProfileScreen(
    repository: ProfileRepository,
    profileId: String? = null,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val isEditing = profileId != null

    // Local state for form fields
    var profileName by remember { mutableStateOf("") }
    var hostUsername by remember { mutableStateOf("") }
    var hostIp by remember { mutableStateOf("") }
    var portText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isDefault by remember { mutableStateOf(false) }

    // If editing, observe profiles to pre-fill initial state from the selected profile
    val profiles by repository.profiles.collectAsState(initial = emptyList())
    val existingProfile = if (isEditing) profiles.find { it.id == profileId } else null
    var didPrefill by remember(profileId) { mutableStateOf(false) }

    LaunchedEffect(existingProfile?.id) {
        if (!didPrefill && existingProfile != null) {
            profileName = existingProfile.name
            hostUsername = existingProfile.username
            hostIp = existingProfile.host
            portText = existingProfile.port.toString()
            password = existingProfile.password
            isDefault = existingProfile.isDefault
            didPrefill = true
        }
    }

    val scrollState = rememberScrollState()
    val shape = RoundedCornerShape(16.dp)
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simple custom header row (no TopAppBar)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (isEditing) "Edit Profile" else "New Profile",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

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

            // Password (immediately under Host Username)
            BorderedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                shape = shape,
                modifier = Modifier.fillMaxWidth(),
                passwordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )

            // Host IP and Port on same row (80% / 20%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Host IP (allow decimal keyboard for dot entry)
                BorderedTextField(
                    value = hostIp,
                    onValueChange = { hostIp = it },
                    label = "Host IP",
                    shape = shape,
                    modifier = Modifier.weight(0.8f),
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
                    modifier = Modifier.weight(0.2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // 'Default' switch row to mark profile as default
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Default", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isDefault,
                    onCheckedChange = { checked -> isDefault = checked }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedButton(
                onClick = {
                    val port = portText.toIntOrNull() ?: 22
                    scope.launch {
                        if (isEditing && profileId != null) {
                            // Update existing profile (no duplicate)
                            repository.updateProfile(
                                id = profileId,
                                name = profileName.trim(),
                                username = hostUsername.trim(),
                                host = hostIp.trim(),
                                port = port,
                                password = password,
                                isDefault = isDefault
                            )
                        } else {
                            // Create new profile
                            repository.addProfile(
                                name = profileName.trim(),
                                username = hostUsername.trim(),
                                host = hostIp.trim(),
                                port = port,
                                password = password,
                                isDefault = isDefault
                            )
                        }
                        onBack()
                    }
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
