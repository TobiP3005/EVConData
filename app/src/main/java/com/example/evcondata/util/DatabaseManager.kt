package com.example.evcondata.util

import android.content.Context
import android.util.Log
import com.couchbase.lite.*

class DatabaseManager {

    private var listenerToken: ListenerToken? = null
    private var currentUser: String? = null

    companion object{
        private var userprofileDatabase: Database? = null
        private var consumptionDatabase: Database? = null

        private const val userProfileDbName = "userprofile"
        private const val consumptionDbName = "consumption"

        private var instance: DatabaseManager? = null

        var syncGatewayEndpoint = "ws://87.152.116.207:4984"

        private val replicator: Replicator? = null
        private val replicatorListenerToken: ListenerToken? = null

        fun getSharedInstance(): DatabaseManager? {
            if (instance == null) {
                instance = DatabaseManager()
            }
            return instance
        }

        fun getUserProfileDatabase(): Database? {
            return userprofileDatabase
        }

        fun getConsumptionDatabase(): Database? {
            return consumptionDatabase
        }
    }

    fun getCurrentUserDocId(): String {
        return "user::$currentUser"
    }

    fun initializeCouchbaseLite(context: Context?) {
        CouchbaseLite.init(context!!)
    }

    fun openOrCreateDatabaseForUser(context: Context, username: String) {
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", context.filesDir, username)
        currentUser = username
        try {
            consumptionDatabase = Database(userProfileDbName, config)
            registerForDatabaseChanges()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    private fun registerForDatabaseChanges() {
        // Add database change listener
        listenerToken = consumptionDatabase!!.addChangeListener { change ->
            for (docId in change.documentIDs) {
                val doc = consumptionDatabase!!.getDocument(
                    docId!!
                )
                if (doc != null) {
                    Log.i("DatabaseChangeEvent", "Document was added/updated")
                } else {
                    Log.i("DatabaseChangeEvent", "Document was deleted")
                }
            }
        }
    }


}