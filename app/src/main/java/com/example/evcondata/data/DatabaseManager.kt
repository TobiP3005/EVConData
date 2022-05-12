package com.example.evcondata.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.auth.UserPreferencesRepository
import java.net.URI
import java.net.URISyntaxException

class DatabaseManager(val context: Context, userPreferencesRepository: UserPreferencesRepository){

    val userPref = userPreferencesRepository

    val syncGatewayEndpoint = "ws://adac-icphig-cbtest.germanywestcentral.cloudapp.azure.com:4984"

    var databases: MutableMap<String, DatabaseResource> = mutableMapOf()
    var evDataDbName = "ev-data"
    private var evDatabase: Database? = null

    private var currentUser: String? = null

    private var replicator: Replicator? = null
    private var replicatorListenerToken: ListenerToken? = null

    init {
        CouchbaseLite.init(context)
    }

    fun initializeDatabase() {
        initEvDataDatabase(context, userPref.userId)
        startPullReplication()
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
        return evDatabase
    }


    private fun initEvDataDatabase(context: Context, username: String) {
        currentUser = username
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", context.filesDir, username)
        try {
            evDatabase = Database(evDataDbName, config)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getEvDataDatabase(): Database? {
        return evDatabase
    }

    // tag::startPushAndPullReplicationForCurrentUser[]
    private fun startPullReplication()
    {
        var url: URI? = null
        try {
            url = URI(
                String.format(
                    "%s/%s",
                    syncGatewayEndpoint,
                    evDataDbName
                )
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val session = userPref.sessionToken
        replicator =
            Replicator(
                ReplicatorConfigurationFactory.create(
                    database = evDatabase,
                    target = URLEndpoint(url!!),
                    type = ReplicatorType.PUSH_AND_PULL,
                    true,
                    authenticator = SessionAuthenticator(session)
                    )
                )

        replicatorListenerToken = replicator!!.addChangeListener { change ->
            if (change.replicator.status.activityLevel == ReplicatorActivityLevel.IDLE) {
                Log.e("Replication Comp Log", "Scheduler Completed")
            }
            if (change.replicator.status.activityLevel == ReplicatorActivityLevel.STOPPED || change.replicator.status.activityLevel == ReplicatorActivityLevel.OFFLINE) {
                Log.e("Rep Scheduler  Log", "ReplicationTag Stopped")
            }
        }

        replicator?.start()
    }
}