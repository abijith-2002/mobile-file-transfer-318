package com.app.quicktransfer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a saved SSH/SFTP connection.
 *
 * Fields:
 * - id: Auto-generated primary key
 * - alias: User-friendly display name
 * - host: Host/IP address
 * - port: SSH port
 * - username: Username for the host
 * - createdAtMillis: Creation timestamp in UTC millis
 */
@Entity(
    tableName = "connections",
    indices = [
        Index(value = ["alias"], unique = false)
    ]
)
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "host")
    val host: String,
    @ColumnInfo(name = "port")
    val port: Int = 22,
    @ColumnInfo(name = "username")
    val username: String = "",
    @ColumnInfo(name = "createdAtMillis")
    val createdAtMillis: Long = System.currentTimeMillis()
)
