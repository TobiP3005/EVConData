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

    suspend fun setSharedConBool(shared: String) {
        dataStore.edit { settings ->
            settings[sharedConBoolPrefKey] = shared
        }
    }

    suspend fun logoutUser() {
        dataStore.edit {
            it.clear()
        }
    }

//    fun getSessionTokenFlow(): Flow<String> = dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                Log.e(TAG, "Error reading preferences: ", exception)
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }
//        .map { pref ->
//            pref[sessionPrefKey] ?: ""
//        }

    val sessionToken =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[sessionPrefKey] ?: ""
            }.first()
        }

    val userId =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[userIdPrefKey] ?: ""
            }.first()
        }

    val username =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[usernamePrefKey]
            }.first()
    }

    val myCar=
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[myCarPrefKey]
            }.first()
        }

    val myCarFlow: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[myCarPrefKey] ?: ""
        }.distinctUntilChanged()

    val sharedConsumption =
        runBlocking {
            dataStore.data.map { preferences ->
                preferences[sharedConBoolPrefKey]
            }.first().toBoolean()
    }

    val sharedConFlow: Flow<String> = dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[sharedConBoolPrefKey] ?: ""
        }
}