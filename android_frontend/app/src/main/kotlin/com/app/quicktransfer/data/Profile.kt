package com.app.quicktransfer.data

// PUBLIC_INTERFACE
data class Profile(
    /** Unique profile ID (UUID/string) */
    val id: String,
    /** Friendly display name */
    val name: String,
    /** Host/IP address */
    val host: String,
    /** SSH port (default 22) */
    val port: Int = 22
)
