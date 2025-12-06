package com.app.quicktransfer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Quick Transfer.
 *
 * Holds the ProfileEntity table and exposes its DAO.
 */
@Database(
    entities = [ProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // PUBLIC_INTERFACE
    /**
     * Provides access to profile data operations.
     */
    abstract fun profileDao(): ProfileDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
