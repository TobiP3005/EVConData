package com.example.evcondata.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.*

class ConsumptionDatabase(val context: Context){

    var databases: MutableMap<String, DatabaseResource> = mutableMapOf()
    var consumptionDbName = "project"
    private var consumptionDatabase: Database? = null

    private var listenerToken: ListenerToken? = null
    private var currentUser: String? = null

    init {
        CouchbaseLite.init(context)
    }

    fun getCurrentUserDocId(): String {
        return "user::$currentUser"
    }

    fun initializeDatabase() {
        openOrCreateConsumptionDatabase(context, "Tobi")
    }

    fun deleteDatabase() {
        try {
            databases.forEach {
                if (Database.exists(it.key, context.filesDir)){
                    it.value.database.close()
                    Database.delete(it.key, context.filesDir)
                }
            }
            databases.clear()
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    fun getConsumptionDatabase(): Database? {
        return consumptionDatabase
    }


    fun openOrCreateConsumptionDatabase(context: Context, username: String) {
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", context.filesDir, username)
        currentUser = username
        try {
            consumptionDatabase = Database(consumptionDbName, config)
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