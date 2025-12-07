package com.app.quicktransfer.data

import android.content.Context
import com.app.quicktransfer.data.local.AppDatabase

// PUBLIC_INTERFACE
/**
 * Simple factory/provider for repositories backed by a Room database instance.
 *
 * Usage:
 * val profileRepo = RepositoryProvider.profileRepository(context)
 * val connectionRepo = RepositoryProvider.connectionRepository(context)
 * val historyRepo = RepositoryProvider.transferHistoryRepository(context)
 */
object RepositoryProvider {

    // PUBLIC_INTERFACE
    /**
     * Provides ProfileRepository using Room where possible, falling back to in-memory if
     * Room initialization fails.
     */
    fun profileRepository(context: Context): ProfileRepository {
        val db = AppDatabase.getInstance(context.applicationContext)
        return ProfileRepository(db.profileDao())
    }

    // PUBLIC_INTERFACE
    /**
     * Provides ConnectionRepository backed by Room.
     */
    fun connectionRepository(context: Context): ConnectionRepository {
        val db = AppDatabase.getInstance(context)
        return ConnectionRepository(db.connectionDao())
    }

    // PUBLIC_INTERFACE
    /**
     * Provides TransferHistoryRepository backed by Room.
     */
    fun transferHistoryRepository(context: Context): TransferHistoryRepository {
        val db = AppDatabase.getInstance(context)
        return TransferHistoryRepository(db.transferHistoryDao())
    }
}
