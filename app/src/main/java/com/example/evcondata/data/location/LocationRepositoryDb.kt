package com.example.evcondata.data.location

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.model.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LocationRepositoryDb(
    databaseManager: DatabaseManager,
    userPreferencesRepository: UserPreferencesRepository
) : LocationRepository {

    private val userPref = userPreferencesRepository
    private val db = databaseManager.getEvDataDatabase()
    private val replicator = databaseManager.getReplicator()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLocationListFlow(): Flow<List<Location>> {
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all()
            )
                .from(it.`as`("item"))
                .where(
                    Expression.property("type").equalTo(Expression.string("location"))
                        .and(
                            Expression.property("car").equalTo(Expression.string(userPref.myCar()))
                        )
                        .and(
                            Expression.property("published").equalTo(Expression.booleanValue(true))
                        )
                        .and(
                            Expression.property("owner")
                                .notEqualTo(Expression.string(userPref.userId()))
                        )
                )
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToLocationList(qc) }
            .flowOn(Dispatchers.IO)

        CoroutineScope(Dispatchers.IO).launch {
            purgeWorkaround()
        }
        return flow
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun purgeWorkaround() {
        val replicatorFlow = replicator?.documentReplicationFlow()
            ?.map { update -> update.documents }
            ?.flowOn(Dispatchers.IO)

        replicatorFlow?.collect { list ->
            val purgeBool = list.any { it.flags.contains(DocumentFlag.ACCESS_REMOVED) }
            if (purgeBool) {
                saveLocation(
                    Location("", "", 1.0, 1.0, ""),
                    "purgeWorkaround"
                ).collect()
                deleteLocation("purgeWorkaround").collect()
            }
        }
    }

    override fun getMyLocation(): LocationModelDTO? {
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all()
            )
                .from(it.`as`("item")).where(
                    Expression.property("type").equalTo(Expression.string("location"))
                        .and(
                            Expression.property("owner")
                                .equalTo(Expression.string(userPref.userId()))
                        )
                )
        }
        val res = query!!
            .execute().map { res -> mapResultToLocation(res) }

        return if (res.isNotEmpty()) {
            res.first()
        } else {
            null
        }
    }

    override fun publishLocation(checked: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            userPref.setSharedLocationBool(checked.toString())
        }

        val docOrigin = db?.getDocument("location-" + userPref.userId())
        if (docOrigin != null) {
            val locationDoc = Gson().fromJson(docOrigin.toJSON(), Location::class.java)
            locationDoc.published = checked
            val json = Gson().toJson(locationDoc)
            val doc = MutableDocument(docOrigin.id, json)
            db?.save(doc)
        }
    }

    override fun isLocationShared(): Boolean {
        return userPref.sharedLocation()
    }

    override fun saveLocation(location: Location, id: String): Flow<ResultCode> = flow {

        var userId = userPref.userId()
        if (id == "purgeWorkaround") {
            "purgeWorkaround".also { userId = it }
        }
        try {
            location.username = userPref.username()
            location.owner = userPref.userId()
            location.car = userPref.myCar()
            location.published = userPref.sharedLocation()
            db?.let { database ->
                val json = Gson().toJson(location)
                val doc = MutableDocument("location-$userId", json)
                database.save(doc)
                emit(ResultCode.SUCCESS)
            }
        } catch (e: Exception) {
            emit(ResultCode.ERROR)
            Log.e(e.message, e.stackTraceToString())
        }
    }.flowOn(Dispatchers.IO)

    override fun updateCar(carName: String) {
        try {
            db?.let {
                val doc = db.getDocument("location-${userPref.userId()}")?.toMutable()
                doc?.setValue("car", carName)
                if (doc != null) {
                    db.save(doc)
                }
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
    }

    override fun deleteLocation(id: String): Flow<ResultCode> = flow {
        try {
            db?.let { database ->
                val projectDoc = database.getDocument("location-$id")
                projectDoc?.let { document ->
                    db.delete(document)
                    emit(ResultCode.SUCCESS)
                }
            }
        } catch (e: java.lang.Exception) {
            emit(ResultCode.ERROR)
            Log.e(e.message, e.stackTraceToString())
        }
    }.flowOn(Dispatchers.IO)

    private fun mapQueryChangeToLocationList(queryChange: QueryChange): List<Location> {
        val locationList = mutableListOf<Location>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                locationList.add(
                    Gson().fromJson(
                        result.toJSON(),
                        LocationModelDTO::class.java
                    ).item
                )
            }
        }
        return locationList
    }

    private fun mapResultToLocation(res: Result): LocationModelDTO? {
        var location: LocationModelDTO?
        res.let { result ->
            location = Gson().fromJson(result.toJSON(), LocationModelDTO::class.java)

        }
        return location
    }
}