package com.app.quicktransfer.data

import com.app.quicktransfer.data.local.ConnectionDao
import com.app.quicktransfer.data.local.ConnectionEntity
import kotlinx.coroutines.flow.Flow

// PUBLIC_INTERFACE
/**
 * Repository for managing SSH/SFTP connections.
 *
 * This minimal repository exposes flows for observation and suspend functions to mutate data.
 */
class ConnectionRepository(
    private val dao: ConnectionDao
) {
    // PUBLIC_INTERFACE
    /**
     * Observes all saved connections, newest first.
     */
    val connections: Flow<List<ConnectionEntity>> = dao.getAll()

    // PUBLIC_INTERFACE
    /**
     * Adds (or replaces) a connection with the provided details.
     *
     * Returns:
     * - The inserted row id.
     */
    suspend fun addConnection(
        alias: String,
        host: String,
        port: Int = 22,
        username: String = ""
    ): Long {
        val entity = ConnectionEntity(
            alias = alias.trim(),
            host = host.trim(),
            port = port,
            username = username.trim()
        )
        return dao.insert(entity)
    }

    // PUBLIC_INTERFACE
    /**
     * Updates an existing connection entity.
     */
    suspend fun updateConnection(entity: ConnectionEntity) {
        dao.update(entity)
    }

    // PUBLIC_INTERFACE
    /**
     * Deletes a connection by primary key id.
     */
    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    // PUBLIC_INTERFACE
    /**
     * Fetches a connection by id, or null if not found.
     */
    suspend fun getById(id: Long): ConnectionEntity? = dao.getById(id)
}
