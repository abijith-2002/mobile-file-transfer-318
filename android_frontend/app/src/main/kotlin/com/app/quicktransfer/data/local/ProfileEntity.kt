package com.app.quicktransfer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a connection profile.
 *
 * Fields:
 * - id: Auto-generated primary key
 * - profileName: Friendly display name
 * - username: SSH username
 * - hostIp: Host/IP address
 * - port: SSH port
 * - password: Plaintext password (temporary; replace with secure storage)
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "profileName")
    val profileName: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "hostIp")
    val hostIp: String,
    @ColumnInfo(name = "port")
    val port: Int,
    @ColumnInfo(name = "password")
    val password: String
)
