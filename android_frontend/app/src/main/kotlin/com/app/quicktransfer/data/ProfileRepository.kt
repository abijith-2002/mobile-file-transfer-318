package com.app.quicktransfer.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileRepository {
    private val _profiles = MutableStateFlow<List<Profile>>(
        listOf(
            Profile(id = "1", name = "Home Server", host = "192.168.1.10", port = 22),
            Profile(id = "2", name = "Workstation", host = "10.0.0.5", port = 22)
        )
    )
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()
}
