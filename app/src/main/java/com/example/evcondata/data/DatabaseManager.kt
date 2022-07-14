package com.example.evcondata.data

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.BuildConfig
import com.example.evcondata.data.auth.UserPreferencesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
        initEvDataDatabase(context, userPref.userId())
        startPushPullReplication()
        waitForReplicator()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun waitForReplicator() {
        val replicatorFlow = replicator?.replicatorChangesFlow()
            ?.map { update -> update.status }
            ?.flowOn(Dispatchers.IO)

        CoroutineScope(Dispatchers.IO).launch {
            replicatorFlow?.cancellable()?.collect { status ->
                if (status.activityLevel == ReplicatorActivityLevel.IDLE) {
                    configureUserProfile()
                    this.cancel()
                }
            }
        }
    }

    private fun configureUserProfile() {
        var myCar: String? = null
        var publishConsumption: Boolean? = null
        var publishLocation: Boolean? = null
        try {
            val userID = userPref.userId()
            val doc = evDataDatabase?.getDocument("userprofile:$userID")
            if (doc != null) {
                myCar = doc.getString("myCar").toString()
                publishConsumption = doc.getBoolean("publishConsumption")
                publishLocation = doc.getBoolean("publishLocation")
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (myCar != null) {
                userPref.setMyCar(myCar)
            }
            if (publishLocation != null) {
                userPref.setSharedLocationBool(publishLocation)
            }
            if (publishConsumption != null) {
                userPref.setSharedConBool(publishConsumption)
            }
        }
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

    private fun startPushPullReplication()
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

        val sessionToken = userPref.sessionToken()
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

    fun checkReplicator() {
        if (getReplicator()?.status?.activityLevel == ReplicatorActivityLevel.STOPPED) {
            startPushPullReplication()
        }
    }
}