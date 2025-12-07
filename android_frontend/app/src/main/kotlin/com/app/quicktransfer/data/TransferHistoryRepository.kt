package com.app.quicktransfer.data

import com.app.quicktransfer.data.local.TransferHistoryDao
import com.app.quicktransfer.data.local.TransferHistoryEntity
import kotlinx.coroutines.flow.Flow

// PUBLIC_INTERFACE
/**
 * Repository for managing transfer history of file uploads/downloads.
 */
class TransferHistoryRepository(
    private val dao: TransferHistoryDao
) {
    // PUBLIC_INTERFACE
    /**
     * Observes all transfer history records.
     */
    val history: Flow<List<TransferHistoryEntity>> = dao.getAll()

    // PUBLIC_INTERFACE
    /**
     * Observes transfer history for a given connection id.
     */
    fun historyForConnection(connectionId: Long): Flow<List<TransferHistoryEntity>> =
        dao.getByConnectionId(connectionId)

    // PUBLIC_INTERFACE
    /**
     * Logs a new transfer start in "IN_PROGRESS" status.
     *
     * Returns:
     * - The inserted row id.
     */
    suspend fun logTransferStart(
        connectionId: Long,
        localPath: String,
        remotePath: String,
        direction: String // "UPLOAD" | "DOWNLOAD"
    ): Long {
        val entity = TransferHistoryEntity(
            connectionId = connectionId,
            localPath = localPath.trim(),
            remotePath = remotePath.trim(),
            direction = direction,
            status = "IN_PROGRESS",
            startedAtMillis = System.currentTimeMillis()
        )
        return dao.insert(entity)
    }

    // PUBLIC_INTERFACE
    /**
     * Marks an existing transfer as SUCCESS or FAILED and finalizes timestamps/bytes.
     *
     * If the transfer row does not exist, this is a no-op.
     */
    suspend fun markTransferComplete(
        id: Long,
        success: Boolean,
        bytesTransferred: Long,
        errorMessage: String? = null
    ) {
        val existing = dao.getById(id) ?: return
        val updated = existing.copy(
            status = if (success) "SUCCESS" else "FAILED",
            completedAtMillis = System.currentTimeMillis(),
            bytesTransferred = bytesTransferred,
            errorMessage = if (success) null else errorMessage
        )
        dao.update(updated)
    }

    // PUBLIC_INTERFACE
    /**
     * Deletes a transfer record by id.
     */
    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}
