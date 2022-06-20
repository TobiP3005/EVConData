package com.example.evcondata.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.BuildConfig
import com.example.evcondata.data.auth.UserPreferencesRepository
import java.net.URI
import java.net.URISyntaxException

class DatabaseManager(val context: Context, userPreferencesRepository: UserPreferencesRepository){

    val userPref = userPreferencesRepository

    var evDataDbName = "ev-data"
    private var evDataDatabase: Database? = null

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

    fun deleteEvDataDatabase() {
        try {
            if (Database.exists(evDataDbName, context.filesDir)){
                evDataDatabase?.close()
                Database.delete(evDataDbName, context.filesDir)
            }
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    fun getConsumptionDatabase(): Database? {
        return evDataDatabase
    }

    fun getReplicator(): Replicator? {
        return replicator
    }

    private fun initEvDataDatabase(context: Context, userId: String) {
        currentUser = userId
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", context.filesDir, userId)
        try {
            evDataDatabase = Database(evDataDbName, config)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getEvDataDatabase(): Database? {
        return evDataDatabase
    }

    // tag::startPushAndPullReplicationForCurrentUser[]
    private fun startPullReplication()
    {
        var url: URI? = null
        try {
            url = URI(
                String.format(
                    "%s/%s",
                    BuildConfig.BASE_URL_WS,
                    evDataDbName
                )
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val sessionToken = userPref.sessionToken
        Replicator(
            ReplicatorConfigurationFactory.create(
                database = evDataDatabase,
                target = URLEndpoint(url!!),
                type = ReplicatorType.PUSH_AND_PULL,
                continuous = true,
                authenticator = SessionAuthenticator(sessionToken),
                enableAutoPurge = true,
                heartbeat = 5
            )
        ).also { replicator = it }

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