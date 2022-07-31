package com.example.evcondata.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    private val sessionPrefKey = stringPreferencesKey("sessionToken")
    private val usernamePrefKey = stringPreferencesKey("username")
    private val myCarPrefKey = stringPreferencesKey("myCar")
    private val userIdPrefKey = stringPreferencesKey("userId")
    private val sharedConBoolPrefKey = stringPreferencesKey("sharedConsumptionBool")
    private val sharedLocationBoolPrefKey = stringPreferencesKey("sharedLocationBool")

    suspend fun setSessionToken(sessionToken: String) {
        dataStore.edit { settings ->
            settings[sessionPrefKey] = sessionToken
        }
    }

    suspend fun setUsername(username: String) {
        dataStore.edit { settings ->
            settings[usernamePrefKey] = username
        }
    }

    suspend fun setMyCar(username: String) {
        dataStore.edit { settings ->
            settings[myCarPrefKey] = username
        }
    }

    suspend fun setUserId(userId: String) {
        dataStore.edit { settings ->
            settings[userIdPrefKey] = userId
        }
    }

    suspend fun setSharedConBool(shared: Boolean) {
        dataStore.edit { settings ->
            settings[sharedConBoolPrefKey] = shared.toString()
        }
    }

    suspend fun setSharedLocationBool(isShared: Boolean) {
        dataStore.edit { settings ->
            settings[sharedLocationBoolPrefKey] = isShared.toString()
        }
    }

    suspend fun logoutUser() {
        dataStore.edit {
            it.clear()
        }
    }

    fun sessionToken(): String =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[sessionPrefKey] ?: ""
            }.first()
        }

    fun userId(): String =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[userIdPrefKey] ?: ""
            }.first()
        }

    fun username(): String =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[usernamePrefKey] ?: ""
            }.first()
        }

    fun myCar(): String = runBlocking {
        dataStore.data.map { preferences ->
            preferences[myCarPrefKey] ?: ""
        }.first()
    }

    val myCarFlow: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[myCarPrefKey] ?: ""
        }.distinctUntilChanged()

    fun sharedConsumption(): Boolean =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[sharedConBoolPrefKey]
            }.first().toBoolean()
        }

    fun sharedLocation(): Boolean =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[sharedLocationBoolPrefKey]
            }.first().toBoolean()
        }


    val sharedConFlow: Flow<String> = dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[sharedConBoolPrefKey] ?: ""
        }
}