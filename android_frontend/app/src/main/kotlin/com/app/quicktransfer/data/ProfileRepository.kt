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
     * - isDefault: If true, mark this profile as the default and unset any previous default
     *
     * Returns:
     * - A synthetic row ID (current time millis), maintained for API compatibility.
     */
    suspend fun addProfile(
        name: String,
        username: String,
        host: String,
        port: Int,
        password: String,
        isDefault: Boolean
    ): Long {
        val newId = UUID.randomUUID().toString()
        val newProfile = Profile(
            id = newId,
            name = name,
            host = host,
            port = port,
            username = username,
            password = password,
            isDefault = isDefault
        )

        // Ensure only one profile is default at a time
        val updated = if (isDefault) {
            listOf(newProfile.copy(isDefault = true)) +
                profilesState.value.map { it.copy(isDefault = false) }
        } else {
            listOf(newProfile.copy(isDefault = false)) + profilesState.value
        }

        // Persist to SharedPreferences
        saveProfilesToPrefs(updated)
        if (isDefault) {
            prefs.edit().putString(DEFAULT_PROFILE_ID_KEY, newId).apply()
        }
        profilesState.value = updated

        // Return a synthetic long id for compatibility (not used by callers)
        return System.currentTimeMillis()
    }

    // PUBLIC_INTERFACE
    /**
     * Returns a profile by its string id or null if not found.
     *
     * Parameters:
     * - id: Profile id (UUID string)
     *
     * Returns:
     * - Profile or null if not found
     */
    fun getProfileById(id: String): Profile? {
        return profilesState.value.find { it.id == id }
    }

    // PUBLIC_INTERFACE
    /**
     * Updates an existing profile with the provided fields.
     *
     * Behavior:
     * - If isDefault is true, marks this profile as the only default and clears default on others.
     * - If the profile was the current default and isDefault is set to false, clears the default id,
     *   resulting in no default profile.
     * - If the id is not found, this is a no-op.
     */
    suspend fun updateProfile(
        id: String,
        name: String,
        username: String,
        host: String,
        port: Int,
        password: String,
        isDefault: Boolean
    ) {
        val existing = profilesState.value
        if (existing.none { it.id == id }) {
            // Nothing to update
            return
        }

        val currentDefaultId = prefs.getString(DEFAULT_PROFILE_ID_KEY, null)
        val updatedList: List<Profile> = if (isDefault) {
            // Set this as the only default
            prefs.edit().putString(DEFAULT_PROFILE_ID_KEY, id).apply()
            existing.map { p ->
                if (p.id == id) {
                    Profile(
                        id = p.id,
                        name = name,
                        host = host,
                        port = port,
                        username = username,
                        password = password,
                        isDefault = true
                    )
                } else {
                    p.copy(isDefault = false)
                }
            }
        } else {
            if (currentDefaultId == id) {
                // Turning off default for the current default; clear default id and ensure none are default
                prefs.edit().remove(DEFAULT_PROFILE_ID_KEY).apply()
                existing.map { p ->
                    if (p.id == id) {
                        Profile(
                            id = p.id,
                            name = name,
                            host = host,
                            port = port,
                            username = username,
                            password = password,
                            isDefault = false
                        )
                    } else {
                        p.copy(isDefault = false)
                    }
                }
            } else {
                // Not affecting the current default; just update fields
                existing.map { p ->
                    if (p.id == id) {
                        Profile(
                            id = p.id,
                            name = name,
                            host = host,
                            port = port,
                            username = username,
                            password = password,
                            isDefault = false
                        )
                    } else {
                        p
                    }
                }
            }
        }

        saveProfilesToPrefs(updatedList)
        profilesState.value = updatedList
    }

    private fun loadProfilesFromPrefs(): List<Profile> {
        val raw = prefs.getString(PROFILES_KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val defaultId = prefs.getString(DEFAULT_PROFILE_ID_KEY, null)
            val result = ArrayList<Profile>(arr.length())
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val id = obj.optString("id", UUID.randomUUID().toString())
                val isDefault = if (defaultId != null) {
                    id == defaultId
                } else {
                    obj.optBoolean("isDefault", false)
                }
                result.add(
                    Profile(
                        id = id,
                        name = obj.optString("name", ""),
                        host = obj.optString("host", ""),
                        port = obj.optInt("port", 22),
                        username = obj.optString("username", ""),
                        password = obj.optString("password", ""),
                        isDefault = isDefault
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
                .put("isDefault", p.isDefault)
            arr.put(obj)
        }
        prefs.edit().putString(PROFILES_KEY, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "profiles_store"
        private const val PROFILES_KEY = "profiles_json"
        private const val DEFAULT_PROFILE_ID_KEY = "default_profile_id"

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
