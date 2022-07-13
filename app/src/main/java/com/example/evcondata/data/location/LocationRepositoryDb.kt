package com.example.evcondata.data.location

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.model.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LocationRepositoryDb(databaseManager: DatabaseManager, userPreferencesRepository: UserPreferencesRepository) : LocationRepository {

    private val userPref = userPreferencesRepository
    private val db = databaseManager.getEvDataDatabase()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLocationListFlow(): Flow<List<Location>> {
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all())
                .from(it.`as`("item"))
                .where(Expression.property("type").equalTo(Expression.string("location"))
                    .and(Expression.property("car").equalTo(Expression.string(userPref.myCar()))
                    .and(Expression.property("owner").notEqualTo(Expression.string(userPref.userId())))))
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToLocationList(qc) }
            .flowOn(Dispatchers.IO)

        query.execute()
        return flow
    }

    override fun getMyLocation(): LocationModelDTO? {
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all())
                .from(it.`as`("item")).where(Expression.property("type").equalTo(Expression.string("location"))
                    .and(Expression.property("owner").equalTo(Expression.string(userPref.userId()))))
        }
        val res = query!!
            .execute().map { res -> mapResultToLocation(res)}

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
        try{
            location.username = userPref.username()
            location.owner = userPref.userId()
            location.car = userPref.myCar()
            location.published = userPref.sharedLocation()
            db?.let { database ->
                val json = Gson().toJson(location)
                val doc = MutableDocument("location-" + userPref.userId(), json)
                database.save(doc)
                emit(ResultCode.SUCCESS)
            }
        } catch (e: Exception){
            emit(ResultCode.ERROR)
            Log.e(e.message, e.stackTraceToString())

        }
    }.flowOn(Dispatchers.IO)

    private fun mapQueryChangeToLocationList(queryChange: QueryChange) : List<Location>{
        val locationList = mutableListOf<Location>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                locationList.add(Gson().fromJson(result.toJSON(), LocationModelDTO::class.java).item)
            }
        }
        return locationList
    }

    private fun mapResultToLocation(res: Result) : LocationModelDTO?{
        var location: LocationModelDTO?
        res.let { result ->
            location = Gson().fromJson(result.toJSON(), LocationModelDTO::class.java)

        }
        return location
    }
}