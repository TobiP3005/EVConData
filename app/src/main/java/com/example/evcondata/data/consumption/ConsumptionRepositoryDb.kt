package com.example.evcondata.data.consumption

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*

class ConsumptionRepositoryDb(private val databaseManager: DatabaseManager, userPreferencesRepository: UserPreferencesRepository) : ConsumptionRepository {

    val userPref = userPreferencesRepository

    override fun getConsumption(id: String): Flow<Consumption?> = flow {
        try {
            val db = databaseManager.getConsumptionDatabase()
            db?.let { database ->
                val doc = database.getDocument(id)
                doc?.let { document  ->
                    emit(Gson().fromJson(document.toJSON(), Consumption::class.java))
                } ?: emit(null)
            }
        } catch (e: Exception) {
            emit(null)
            Log.e(e.message, e.stackTraceToString())
            }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getConsumptionListFlow(): Flow<List<ConsumptionModelDTO>> {
        val db = databaseManager.getConsumptionDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all())
                .from(it.`as`("item"))
                .where(Expression.property("type").equalTo(Expression.string("consumption"))
                    .and(Expression.property("owner").equalTo(Expression.string(userPref.userId))))
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToConsumptionList(qc) }
            .flowOn(Dispatchers.IO)

        query.execute()
        return flow
    }

    private fun mapQueryChangeToConsumptionList(queryChange: QueryChange) : List<ConsumptionModelDTO>{
        val consumptionList = mutableListOf<ConsumptionModelDTO>()
        queryChange.results?.let { results ->
            results.forEach() { result ->
                consumptionList.add(Gson().fromJson(result.toJSON(), ConsumptionModelDTO::class.java))
            }
        }
        return consumptionList
    }

    override fun saveConsumption(consumption: Consumption, id: String): Flow<ResultCode> = flow {
        try{
            consumption.owner = userPref.userId
            val db = databaseManager.getConsumptionDatabase()
            db?.let { database ->
                val json = Gson().toJson(consumption)
                val doc = MutableDocument(id, json)
                database.save(doc)
                emit(ResultCode.SUCCESS)
            }
        } catch (e: Exception){
            emit(ResultCode.ERROR)
            Log.e(e.message, e.stackTraceToString())

        }
    }.flowOn(Dispatchers.IO)

    override fun deleteConsumption(id: String): Flow<ResultCode> = flow {
        try {
            val db = databaseManager.getConsumptionDatabase()
            db?.let { database ->
                val projectDoc = database.getDocument(id)
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

    override fun initializeDatabase() {
        return databaseManager.initializeDatabase()
    }

    override suspend fun deleteDatabase() {
        return withContext(Dispatchers.IO){
            return@withContext databaseManager.deleteDatabase()
        }
    }
}