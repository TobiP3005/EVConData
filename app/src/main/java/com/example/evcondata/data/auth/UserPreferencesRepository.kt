package com.example.evcondata.data.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){

    private val sessionPrefKey = stringPreferencesKey("sessionToken")
    private val usernamePrefKey = stringPreferencesKey("username")

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

    fun getSessionTokenFlow(): Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { pref ->
            pref[sessionPrefKey] ?: ""
        }

    fun getSessionToken() {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[sessionPrefKey]
            }
        }
    }

    fun getUsername() {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[usernamePrefKey]
            }
        }
    }
}