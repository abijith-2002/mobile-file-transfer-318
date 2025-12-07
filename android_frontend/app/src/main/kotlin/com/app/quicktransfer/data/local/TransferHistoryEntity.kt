package com.app.quicktransfer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a file transfer history record.
 *
 * Fields:
 * - id: Auto-generated primary key
 * - connectionId: Related connection primary key
 * - localPath: Local device path
 * - remotePath: Remote host path
 * - direction: "UPLOAD" or "DOWNLOAD"
 * - status: "PENDING", "IN_PROGRESS", "SUCCESS", "FAILED"
 * - bytesTransferred: Total bytes transferred
 * - startedAtMillis: When the transfer started (UTC millis)
 * - completedAtMillis: When the transfer completed (UTC millis), nullable
 * - errorMessage: Optional error details if failed
 */
@Entity(
    tableName = "transfer_history",
    indices = [
        Index(value = ["connectionId"])
    ]
)
data class TransferHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "connectionId")
    val connectionId: Long,
    @ColumnInfo(name = "localPath")
    val localPath: String,
    @ColumnInfo(name = "remotePath")
    val remotePath: String,
    @ColumnInfo(name = "direction")
    val direction: String, // "UPLOAD" | "DOWNLOAD"
    @ColumnInfo(name = "status")
    val status: String = "PENDING", // "PENDING" | "IN_PROGRESS" | "SUCCESS" | "FAILED"
    @ColumnInfo(name = "bytesTransferred")
    val bytesTransferred: Long = 0,
    @ColumnInfo(name = "startedAtMillis")
    val startedAtMillis: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "completedAtMillis")
    val completedAtMillis: Long? = null,
    @ColumnInfo(name = "errorMessage")
    val errorMessage: String? = null
)
