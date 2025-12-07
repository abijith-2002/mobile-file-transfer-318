package com.app.quicktransfer.data

import android.content.Context
import com.app.quicktransfer.data.local.AppDatabase

// PUBLIC_INTERFACE
/**
 * Simple factory/provider for repositories.
 *
 * Profiles are backed by SharedPreferences (replacing Room).
 * Connections and Transfer History remain backed by Room.
 *
 * Usage:
 * val profileRepo = RepositoryProvider.profileRepository(context)
 * val connectionRepo = RepositoryProvider.connectionRepository(context)
 * val historyRepo = RepositoryProvider.transferHistoryRepository(context)
 */
object RepositoryProvider {

    // PUBLIC_INTERFACE
    /**
     * Provides ProfileRepository backed by SharedPreferences.
     */
    fun profileRepository(context: Context): ProfileRepository {
        return ProfileRepository.getInstance(context.applicationContext)
    }

    // PUBLIC_INTERFACE
    /**
     * Provides ConnectionRepository backed by Room.
     */
    fun connectionRepository(context: Context): ConnectionRepository {
        val db = AppDatabase.getInstance(context.applicationContext)
        return ConnectionRepository(db.connectionDao())
    }

    // PUBLIC_INTERFACE
    /**
     * Provides TransferHistoryRepository backed by Room.
     */
    fun transferHistoryRepository(context: Context): TransferHistoryRepository {
        val db = AppDatabase.getInstance(context.applicationContext)
        return TransferHistoryRepository(db.transferHistoryDao())
    }
}
