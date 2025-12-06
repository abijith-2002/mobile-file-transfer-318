package com.app.quicktransfer.data

import com.app.quicktransfer.data.local.ProfileDao
import com.app.quicktransfer.data.local.ProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// PUBLIC_INTERFACE
/**
 * Repository backed by Room for managing connection profiles.
 *
 * Exposes a Flow<List<Profile>> for observation and suspend functions for mutations.
 */
class ProfileRepository(
    private val dao: ProfileDao
) {

    // PUBLIC_INTERFACE
    /**
     * Observes all profiles from the database as domain models.
     */
    val profiles: Flow<List<Profile>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    // PUBLIC_INTERFACE
    /**
     * Persists a new profile in the database.
     *
     * Parameters:
     * - name: Display name
     * - username: SSH username
     * - host: Host/IP address
     * - port: SSH port
     * - password: Plaintext password (temporary; replace with secure storage)
     *
     * Returns:
     * - The inserted row ID.
     */
    suspend fun addProfile(
        name: String,
        username: String,
        host: String,
        port: Int,
        password: String
    ): Long {
        val entity = ProfileEntity(
            profileName = name,
            username = username,
            hostIp = host,
            port = port,
            password = password
        )
        return dao.insert(entity)
    }
}

/**
 * Maps a Room entity to the domain model.
 */
private fun ProfileEntity.toDomain(): Profile =
    Profile(
        id = id.toString(),
        name = profileName,
        host = hostIp,
        port = port,
        username = username,
        password = password
    )
