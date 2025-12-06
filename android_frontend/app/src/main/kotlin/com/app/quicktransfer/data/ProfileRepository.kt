package com.app.quicktransfer.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Simple in-memory repository for profiles shared across the app.
 * This is a lightweight store; replace with persistent storage (e.g., DataStore) later as needed.
 */
object ProfileRepository {
    private val _profiles = MutableStateFlow<List<Profile>>(
        listOf(
            Profile(id = "1", name = "Home Server", host = "192.168.1.10", port = 22),
            Profile(id = "2", name = "Workstation", host = "10.0.0.5", port = 22)
        )
    )
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    // PUBLIC_INTERFACE
    /**
     * Adds a new profile to the repository and emits updated list.
     *
     * @param name Display name of the profile.
     * @param username Host username.
     * @param host Host/IP address.
     * @param port SSH port.
     * @param password Password for the host.
     * @param id Optional fixed ID; if blank a UUID is generated.
     * @return The created Profile.
     */
    fun addProfile(
        name: String,
        username: String,
        host: String,
        port: Int,
        password: String,
        id: String = ""
    ): Profile {
        val profile = Profile(
            id = if (id.isNotBlank()) id else UUID.randomUUID().toString(),
            name = name,
            host = host,
            port = port,
            username = username,
            password = password
        )
        _profiles.value = _profiles.value + profile
        return profile
    }
}
