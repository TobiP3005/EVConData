package com.example.evcondata.data.car

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.model.Car
import com.example.evcondata.model.CarModelDTO
import com.example.evcondata.model.UserProfile
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CarRepositoryDb(
    private val databaseManager: DatabaseManager,
    userPreferencesRepository: UserPreferencesRepository
) : CarRepository {

    private val userPref = userPreferencesRepository

    init {
        waitForReplicator()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun waitForReplicator() {
        val replicator = databaseManager.getReplicator()
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
        try {
            val userID = userPref.userId()
            val db = databaseManager.getConsumptionDatabase()
            val doc = db?.getDocument("userprofile:$userID")
            if (doc != null) {
                myCar = doc.getString("myCar").toString()
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }

        CoroutineScope(Dispatchers.IO).launch {
            myCar?.let { userPref.setMyCar(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCarListFlow(): Flow<List<Car>> {
        val db = databaseManager.getEvDataDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all()
            )
                .from(it.`as`("item"))
                .where(Expression.property("type").equalTo(Expression.string("car")))
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToCarList(qc) }
            .flowOn(Dispatchers.IO)

        query.execute()
        return flow
    }

    override fun getMyCar(): Car? {
        val db = databaseManager.getEvDataDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all()
            )
                .from(it.`as`("item"))
                .where(
                    Expression.property("name").equalTo(Expression.string(userPref.myCar()))
                        .and(Expression.property("type").equalTo(Expression.string("car")))
                )
                .limit(Expression.intValue(1))
        }
        val car = query!!.execute()
            .map { qc -> mapQueryChangeToCar(qc) }

        if (car.isEmpty()) {
            return null
        }
        return car.first()
    }

    override fun getCar(carName: String): Car? {
        val db = databaseManager.getEvDataDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all()
            )
                .from(it.`as`("item"))
                .where(
                    Expression.property("name").equalTo(Expression.string(carName))
                        .and(Expression.property("type").equalTo(Expression.string("car")))
                )
                .limit(Expression.intValue(1))
        }
        val car = query!!.execute()
            .map { qc -> mapQueryChangeToCar(qc) }

        if (car.isEmpty()) {
            return null
        }
        return car.first()
    }

    override fun setMyCar(myCar: String) {
        val userID = userPref.userId()
        val db = databaseManager.getConsumptionDatabase()
        val docOrigin = db?.getDocument("userprofile:$userID")
        if (docOrigin != null) {
            val userProfile = Gson().fromJson(docOrigin.toJSON(), UserProfile::class.java)
            userProfile.myCar = myCar
            val json = Gson().toJson(userProfile)
            val doc = MutableDocument(docOrigin.id, json)
            db.save(doc)
        } else {
            val userProfile = UserProfile(false, myCar, userID)
            val json = Gson().toJson(userProfile)
            val doc = MutableDocument("userprofile:$userID", json)
            db?.save(doc)
        }

        CoroutineScope(Dispatchers.IO).launch {
            myCar.let { userPref.setMyCar(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCarNames(): Flow<List<String>> {
        val db = databaseManager.getConsumptionDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.selectDistinct(
                SelectResult.expression(Expression.property("name"))
            )
                .from(it)
                .where(
                    Expression.property("type").equalTo(Expression.string("car"))
                )
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> getCarNames(qc) }
            .flowOn(Dispatchers.IO)

        val rs = query.execute()
        val carNamesList = mutableListOf<String>()
        for (result in rs) {
            result.getString("name")?.let { carNamesList.add(it) }
        }
        return flow
    }

    private fun getCarNames(queryChange: QueryChange): List<String> {
        val carList = mutableListOf<String>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                result.getString("name")?.let { carList.add(it) }
            }
        }
        return carList
    }

    private fun mapQueryChangeToCarList(queryChange: QueryChange): List<Car> {
        val carList = mutableListOf<Car>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                carList.add(Gson().fromJson(result.toJSON(), CarModelDTO::class.java).item)
            }
        }
        return carList
    }

    private fun mapQueryChangeToCar(queryChange: Result): Car? {
        var car: Car?
        queryChange.let { result ->
            car = Gson().fromJson(result.toJSON(), CarModelDTO::class.java).item
        }
        return car
    }
}