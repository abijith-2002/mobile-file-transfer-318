package com.app.quicktransfer.data

// PUBLIC_INTERFACE
/**
 * Represents a connection profile for SSH/SFTP.
 *
 * Fields:
 * - id: Unique profile ID (UUID/string)
 * - name: Friendly display name
 * - host: Host/IP address
 * - port: SSH port (default 22)
 * - username: Host username (optional)
 * - password: Host password (optional, stored in plaintext for now)
 * - isDefault: Whether this profile is marked as the default profile
 */
data class Profile(
    /** Unique profile ID (UUID/string) */
    val id: String,
    /** Friendly display name */
    val name: String,
    /** Host/IP address */
    val host: String,
    /** SSH port (default 22) */
    val port: Int = 22,
    /** Username on the host for SSH/SFTP */
    val username: String = "",
    /** Plaintext password (temporary; replace with secure storage later) */
    val password: String = "",
    /** Indicates if this profile is the default one */
    val isDefault: Boolean = false
)
