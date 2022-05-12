package com.example.evcondata.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){

    private val sessionPrefKey = stringPreferencesKey("sessionToken")
    private val usernamePrefKey = stringPreferencesKey("username")
    private val userIdPrefKey = stringPreferencesKey("userId")

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

    suspend fun setUserId(userId: String) {
        dataStore.edit { settings ->
            settings[userIdPrefKey] = userId
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
            }
    }
}