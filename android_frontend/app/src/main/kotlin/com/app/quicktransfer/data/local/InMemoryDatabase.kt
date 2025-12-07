package com.app.quicktransfer.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

/**
 * In-memory fallback AppDatabase used when Room's generated implementation is unavailable.
 *
 * This object provides functional DAO implementations backed by in-memory state flows, allowing
 * the application to start and operate without a physical database. It is only used as a fallback.
 */
internal object InMemoryAppDatabase : AppDatabase() {
    private val profileDaoImpl = InMemoryProfileDao()
    private val connectionDaoImpl = InMemoryConnectionDao()
    private val transferHistoryDaoImpl = InMemoryTransferHistoryDao()

    override fun profileDao(): ProfileDao = profileDaoImpl
    override fun connectionDao(): ConnectionDao = connectionDaoImpl
    override fun transferHistoryDao(): TransferHistoryDao = transferHistoryDaoImpl

    override fun createInvalidationTracker(): InvalidationTracker {
        // No-op invalidation tracker for in-memory fallback
        return InvalidationTracker(
            this,
            emptyMap(),
            emptyMap(),
            "profiles",
            "connections",
            "transfer_history"
        )
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        // No underlying SQLite database in fallback; return a no-op helper
        return object : SupportSQLiteOpenHelper {
            override fun getDatabaseName(): String? = "in_memory"
            override fun setWriteAheadLoggingEnabled(enabled: Boolean) { /* no-op */ }
            override fun getWritableDatabase(): SupportSQLiteDatabase {
                throw UnsupportedOperationException("InMemory fallback has no SQLite database")
            }
            override fun getReadableDatabase(): SupportSQLiteDatabase = getWritableDatabase()
            override fun close() { /* no-op */ }
        }
    }

    /**
     * Clears all in-memory tables (profiles, connections, transfer history).
     * This satisfies the abstract RoomDatabase contract for clearAllTables().
     */
    override fun clearAllTables() {
        profileDaoImpl.clear()
        connectionDaoImpl.clear()
        transferHistoryDaoImpl.clear()
    }
}

/**
 * In-memory implementation of ConnectionDao.
 */
internal class InMemoryConnectionDao : ConnectionDao {
    private val itemsState = MutableStateFlow<List<ConnectionEntity>>(emptyList())
    private var nextId = 1L

    override fun getAll(): Flow<List<ConnectionEntity>> =
        itemsState.asStateFlow().map { list -> list.sortedByDescending { it.id } }

    override suspend fun getById(id: Long): ConnectionEntity? =
        itemsState.value.find { it.id == id }

    override suspend fun findByAlias(alias: String): ConnectionEntity? =
        itemsState.value.find { it.alias == alias }

    override suspend fun insert(entity: ConnectionEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        val stored = entity.copy(id = id)
        // Prepend newest to mimic ORDER BY id DESC
        itemsState.value = listOf(stored) + itemsState.value.filterNot { it.id == id }
        return id
    }

    override suspend fun update(entity: ConnectionEntity) {
        itemsState.value = itemsState.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun delete(entity: ConnectionEntity) {
        itemsState.value = itemsState.value.filterNot { it.id == entity.id }
    }

    override suspend fun deleteById(id: Long) {
        itemsState.value = itemsState.value.filterNot { it.id == id }
    }

    /**
     * Clears all in-memory connection rows and resets id counter.
     */
    fun clear() {
        itemsState.value = emptyList()
        nextId = 1L
    }
}

/**
 * In-memory implementation of TransferHistoryDao.
 */
internal class InMemoryTransferHistoryDao : TransferHistoryDao {
    private val itemsState = MutableStateFlow<List<TransferHistoryEntity>>(emptyList())
    private var nextId = 1L

    override fun getAll(): Flow<List<TransferHistoryEntity>> =
        itemsState.asStateFlow().map { list -> list.sortedByDescending { it.startedAtMillis } }

    override fun getByConnectionId(connectionId: Long): Flow<List<TransferHistoryEntity>> =
        itemsState.asStateFlow().map { list ->
            list.filter { it.connectionId == connectionId }
                .sortedByDescending { it.startedAtMillis }
        }

    override fun getRecent(limit: Int): Flow<List<TransferHistoryEntity>> =
        itemsState.asStateFlow().map { list ->
            list.sortedByDescending { it.startedAtMillis }.take(limit)
        }

    override suspend fun getById(id: Long): TransferHistoryEntity? =
        itemsState.value.find { it.id == id }

    override suspend fun insert(entity: TransferHistoryEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        val stored = entity.copy(id = id)
        // Prepend newest by startedAtMillis DESC; simply add to front
        itemsState.value = listOf(stored) + itemsState.value.filterNot { it.id == id }
        return id
    }

    override suspend fun update(entity: TransferHistoryEntity) {
        itemsState.value = itemsState.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun delete(entity: TransferHistoryEntity) {
        itemsState.value = itemsState.value.filterNot { it.id == entity.id }
    }

    override suspend fun deleteById(id: Long) {
        itemsState.value = itemsState.value.filterNot { it.id == id }
    }

    /**
     * Clears all in-memory transfer history rows and resets id counter.
     */
    fun clear() {
        itemsState.value = emptyList()
        nextId = 1L
    }
}
