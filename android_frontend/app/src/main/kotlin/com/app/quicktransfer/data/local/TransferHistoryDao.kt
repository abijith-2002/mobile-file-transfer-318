package com.app.quicktransfer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for TransferHistoryEntity.
 */
@Dao
interface TransferHistoryDao {

    // PUBLIC_INTERFACE
    /**
     * Observes all transfer history records ordered by start time descending.
     */
    @Query("SELECT * FROM transfer_history ORDER BY startedAtMillis DESC")
    fun getAll(): Flow<List<TransferHistoryEntity>>

    // PUBLIC_INTERFACE
    /**
     * Observes transfer history for a specific connection, ordered by start time descending.
     */
    @Query("SELECT * FROM transfer_history WHERE connectionId = :connectionId ORDER BY startedAtMillis DESC")
    fun getByConnectionId(connectionId: Long): Flow<List<TransferHistoryEntity>>

    // PUBLIC_INTERFACE
    /**
     * Provides a stream of recent transfers limited by the given count.
     */
    @Query("SELECT * FROM transfer_history ORDER BY startedAtMillis DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TransferHistoryEntity>>

    // PUBLIC_INTERFACE
    /**
     * Fetches a transfer by id.
     */
    @Query("SELECT * FROM transfer_history WHERE id = :id")
    suspend fun getById(id: Long): TransferHistoryEntity?

    // PUBLIC_INTERFACE
    /**
     * Inserts or replaces a transfer history record.
     *
     * Returns:
     * - RowId of the inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransferHistoryEntity): Long

    // PUBLIC_INTERFACE
    /**
     * Updates an existing transfer record.
     */
    @Update
    suspend fun update(entity: TransferHistoryEntity)

    // PUBLIC_INTERFACE
    /**
     * Deletes a transfer record by entity.
     */
    @Delete
    suspend fun delete(entity: TransferHistoryEntity)

    // PUBLIC_INTERFACE
    /**
     * Deletes a transfer record by id.
     */
    @Query("DELETE FROM transfer_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
