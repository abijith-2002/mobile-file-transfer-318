package com.app.quicktransfer.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory implementation of ProfileDao.
 *
 * This implementation stores profiles in memory for the lifetime of the process
 * and exposes changes through a cold Flow. It's intended as a safe fallback when
 * Room initialization fails so the app can still start and function.
 */
class InMemoryProfileDao : ProfileDao {
    private val itemsState = MutableStateFlow<List<ProfileEntity>>(emptyList())
    private var nextId = 1L

    // PUBLIC_INTERFACE
    /**
     * Observes all profiles as a cold Flow, newest first (DESC by id).
     */
    override fun getAll(): Flow<List<ProfileEntity>> = itemsState.asStateFlow()

    // PUBLIC_INTERFACE
    /**
     * Inserts a profile, auto-assigning an id if needed.
     */
    override suspend fun insert(entity: ProfileEntity): Long {
        val id = if (entity.id == 0L) {
            nextId++
            nextId - 1
        } else {
            entity.id
        }
        val stored = entity.copy(id = id)
        // Prepend newest to mimic ORDER BY id DESC
        itemsState.value = listOf(stored) + itemsState.value
        return id
    }

    // PUBLIC_INTERFACE
    /**
     * Deletes the provided entity by id.
     */
    override suspend fun delete(entity: ProfileEntity) {
        itemsState.value = itemsState.value.filterNot { it.id == entity.id }
    }

    // PUBLIC_INTERFACE
    /**
     * Deletes a profile by id.
     */
    override suspend fun deleteById(id: Long) {
        itemsState.value = itemsState.value.filterNot { it.id == id }
    }

    // PUBLIC_INTERFACE
    /**
     * Fetches a profile by id, or null if not found.
     */
    override suspend fun getById(id: Long): ProfileEntity? {
        return itemsState.value.find { it.id == id }
    }

    /**
     * Clears all in-memory profiles and resets id counter.
     */
    fun clear() {
        itemsState.value = emptyList()
        nextId = 1L
    }
}
