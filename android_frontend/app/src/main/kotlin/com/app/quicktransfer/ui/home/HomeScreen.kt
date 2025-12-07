package com.app.quicktransfer.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.quicktransfer.data.Profile
import com.app.quicktransfer.data.ProfileRepository
import com.app.quicktransfer.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

// PUBLIC_INTERFACE
/**
 * Home screen that lists saved connection profiles with an add-profile FAB (no top app bar).
 *
 * Parameters:
 * - repository: Profile repository used to observe profiles.
 * - onAddProfile: Called when the user taps the add action.
 * - onOpenProfile: Called when the user taps a profile card.
 *
 * Behavior:
 * - Observes profiles from the repository and renders a list of cards.
 * - Each card shows Profile Name and IP address.
 * - Default profiles display a small 'Default' badge on the right side of the card.
 *
 * Returns:
 * - None. Renders the UI.
 */
@Composable
fun HomeScreen(
    repository: ProfileRepository,
    onAddProfile: () -> Unit,
    onOpenProfile: (Profile) -> Unit
) {
    val profiles by repository.profiles.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProfile) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add profile")
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Connections",
                style = MaterialTheme.typography.headlineSmall
            )

            profiles.forEach { profile ->
                ProfileCard(
                    profile = profile,
                    onClick = { onOpenProfile(profile) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: Profile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = profile.host, // Show IP/Host
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (profile.isDefault) {
                DefaultBadge()
            }
        }
    }
}

@Composable
private fun DefaultBadge(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = "Default",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private class SampleProfilesProvider : PreviewParameterProvider<List<Profile>> {
    override val values: Sequence<List<Profile>> = sequenceOf(
        listOf(
            Profile("1", "Home Server", "192.168.1.10", 22, isDefault = true),
            Profile("2", "Workstation", "10.0.0.5", 22)
        )
    )
}

@Preview(name = "Home Light", showBackground = true)
@Composable
private fun HomePreviewLight(
    @PreviewParameter(SampleProfilesProvider::class) samples: List<Profile>
) {
    AppTheme(dynamicColor = false) {
        Scaffold { padding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Connections",
                    style = MaterialTheme.typography.headlineSmall
                )
                samples.forEach { profile ->
                    ProfileCard(profile = profile, onClick = {})
                }
            }
        }
    }
}

@Preview(name = "Home Dark", showBackground = true)
@Composable
private fun HomePreviewDark(
    @PreviewParameter(SampleProfilesProvider::class) samples: List<Profile>
) {
    AppTheme(dynamicColor = false, useDarkTheme = true) {
        Scaffold { padding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Connections",
                    style = MaterialTheme.typography.headlineSmall
                )
                samples.forEach { profile ->
                    ProfileCard(profile = profile, onClick = {})
                }
            }
        }
    }
}
