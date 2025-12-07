package com.app.quicktransfer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Quick Transfer.
 *
 * Holds the ProfileEntity table and additional tables for connections and transfer history,
 * and exposes DAOs for data access.
 */
@Database(
    entities = [
        ProfileEntity::class,
        ConnectionEntity::class,
        TransferHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // PUBLIC_INTERFACE
    /**
     * Provides access to profile data operations.
     */
    abstract fun profileDao(): ProfileDao

    // PUBLIC_INTERFACE
    /**
     * Provides access to connection data operations.
     */
    abstract fun connectionDao(): ConnectionDao

    // PUBLIC_INTERFACE
    /**
     * Provides access to transfer history data operations.
     */
    abstract fun transferHistoryDao(): TransferHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // PUBLIC_INTERFACE
        /**
         * Returns the singleton instance of the database.
         *
         * Parameters:
         * - context: Application context
         *
         * Returns:
         * - AppDatabase singleton
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quick_transfer.db"
                )
                    // For early development we prefer destructive migration if the schema changes.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
