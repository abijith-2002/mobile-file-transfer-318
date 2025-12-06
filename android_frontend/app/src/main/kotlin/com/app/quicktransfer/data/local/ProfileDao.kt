package com.app.quicktransfer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for ProfileEntity.
 */
@Dao
interface ProfileDao {

    // PUBLIC_INTERFACE
    /**
     * Observes all profiles as a cold Flow.
     *
     * Returns:
     * - Flow emitting the list of profiles ordered by id descending
     */
    @Query("SELECT * FROM profiles ORDER BY id DESC")
    fun getAll(): Flow<List<ProfileEntity>>

    // PUBLIC_INTERFACE
    /**
     * Inserts or replaces a profile.
     *
     * Parameters:
     * - entity: Profile to persist
     *
     * Returns:
     * - RowId of the inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ProfileEntity): Long

    // PUBLIC_INTERFACE
    /**
     * Deletes a profile by entity.
     */
    @Delete
    suspend fun delete(entity: ProfileEntity)

    // PUBLIC_INTERFACE
    /**
     * Deletes a profile by id.
     */
    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteById(id: Long)

    // PUBLIC_INTERFACE
    /**
     * Fetches a profile by id.
     */
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getById(id: Long): ProfileEntity?
}
