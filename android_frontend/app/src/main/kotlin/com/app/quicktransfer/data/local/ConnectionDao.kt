package com.app.quicktransfer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for ConnectionEntity.
 */
@Dao
interface ConnectionDao {

    // PUBLIC_INTERFACE
    /**
     * Observes all connections ordered by id DESC (newest first).
     *
     * Returns:
     * - Flow emitting the list of connections.
     */
    @Query("SELECT * FROM connections ORDER BY id DESC")
    fun getAll(): Flow<List<ConnectionEntity>>

    // PUBLIC_INTERFACE
    /**
     * Fetches a connection by id.
     *
     * Parameters:
     * - id: Primary key
     */
    @Query("SELECT * FROM connections WHERE id = :id")
    suspend fun getById(id: Long): ConnectionEntity?

    // PUBLIC_INTERFACE
    /**
     * Finds a connection by its alias (first match).
     *
     * Parameters:
     * - alias: Display name
     */
    @Query("SELECT * FROM connections WHERE alias = :alias LIMIT 1")
    suspend fun findByAlias(alias: String): ConnectionEntity?

    // PUBLIC_INTERFACE
    /**
     * Inserts or replaces a connection.
     *
     * Returns:
     * - RowId of the inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConnectionEntity): Long

    // PUBLIC_INTERFACE
    /**
     * Updates an existing connection entity.
     */
    @Update
    suspend fun update(entity: ConnectionEntity)

    // PUBLIC_INTERFACE
    /**
     * Deletes a connection by entity.
     */
    @Delete
    suspend fun delete(entity: ConnectionEntity)

    // PUBLIC_INTERFACE
    /**
     * Deletes a connection by id.
     */
    @Query("DELETE FROM connections WHERE id = :id")
    suspend fun deleteById(id: Long)
}
