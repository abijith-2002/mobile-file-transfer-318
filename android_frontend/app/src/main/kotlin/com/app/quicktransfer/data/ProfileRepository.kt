package com.app.quicktransfer.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing connection profiles, backed by SharedPreferences.
 *
 * This implementation replaces the previous Room-backed logic for profiles only.
 * It persists the list of profiles as a JSON array in SharedPreferences and exposes
 * a Flow<List<Profile>> for observation. New profiles are prepended to the list
 * (newest first) to keep the UI consistent with previous behavior.
 */
class ProfileRepository private constructor(
    private val prefs: SharedPreferences
) {

    private val profilesState = MutableStateFlow<List<Profile>>(emptyList())

    init {
        // Load persisted profiles
        profilesState.value = loadProfilesFromPrefs()
    }

    // PUBLIC_INTERFACE
    /**
     * Observes all profiles as a Flow.
     *
     * Returns:
     * - Flow emitting the list of profiles (newest first).
     */
    val profiles: Flow<List<Profile>> = profilesState.asStateFlow()

    // PUBLIC_INTERFACE
    /**
     * Persists a new profile in SharedPreferences.
     *
     * Parameters:
     * - name: Display name
     * - username: SSH username
     * - host: Host/IP address
     * - port: SSH port
     * - password: Plaintext password (temporary; replace with secure storage)
     *
     * Returns:
     * - A synthetic row ID (current time millis), maintained for API compatibility.
     */
    suspend fun addProfile(
        name: String,
        username: String,
        host: String,
        port: Int,
        password: String
    ): Long {
        val newProfile = Profile(
            id = UUID.randomUUID().toString(),
            name = name,
            host = host,
            port = port,
            username = username,
            password = password
        )

        // Update in-memory state (prepend newest)
        val updated = listOf(newProfile) + profilesState.value
        // Persist to SharedPreferences
        saveProfilesToPrefs(updated)
        profilesState.value = updated

        // Return a synthetic long id for compatibility (not used by callers)
        return System.currentTimeMillis()
    }

    private fun loadProfilesFromPrefs(): List<Profile> {
        val raw = prefs.getString(PROFILES_KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val result = ArrayList<Profile>(arr.length())
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                result.add(
                    Profile(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        name = obj.optString("name", ""),
                        host = obj.optString("host", ""),
                        port = obj.optInt("port", 22),
                        username = obj.optString("username", ""),
                        password = obj.optString("password", "")
                    )
                )
            }
            result
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun saveProfilesToPrefs(list: List<Profile>) {
        val arr = JSONArray()
        list.forEach { p ->
            val obj = JSONObject()
                .put("id", p.id)
                .put("name", p.name)
                .put("host", p.host)
                .put("port", p.port)
                .put("username", p.username)
                .put("password", p.password)
            arr.put(obj)
        }
        prefs.edit().putString(PROFILES_KEY, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "profiles_store"
        private const val PROFILES_KEY = "profiles_json"

        @Volatile
        private var INSTANCE: ProfileRepository? = null

        // PUBLIC_INTERFACE
        /**
         * Returns a singleton instance of ProfileRepository backed by SharedPreferences.
         *
         * Parameters:
         * - context: Application context
         *
         * Returns:
         * - ProfileRepository singleton
         */
        fun getInstance(context: Context): ProfileRepository {
            return INSTANCE ?: synchronized(this) {
                val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val instance = ProfileRepository(prefs)
                INSTANCE = instance
                instance
            }
        }
    }
}
