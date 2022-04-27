package com.example.evcondata.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.*

class DatabaseManager(val context: Context){

    var databases: MutableMap<String, DatabaseResource> = mutableMapOf()
    var consumptionDbName = "consumptionDb"
    private var consumptionDatabase: Database? = null

    var carDbName = "carDb"
    private var carDatabase: Database? = null

    private var listenerToken: ListenerToken? = null
    private var currentUser: String? = null

    init {
        CouchbaseLite.init(context)
    }

    fun getCurrentUserDocId(): String {
        return "user::$currentUser"
    }

    fun initializeDatabase() {
        initConsumptionDatabase(context, "Tobi")
        initCarDatabase(context)
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


    private fun initConsumptionDatabase(context: Context, username: String) {
        currentUser = username
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", context.filesDir, username)
        try {
            consumptionDatabase = Database(consumptionDbName, config)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getCarDatabase(): Database? {
        return carDatabase
    }

    private fun initCarDatabase(context: Context) {
        val config = DatabaseConfiguration()
        config.directory = context.filesDir.toString()
        try {
            carDatabase = Database(carDbName, config)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}