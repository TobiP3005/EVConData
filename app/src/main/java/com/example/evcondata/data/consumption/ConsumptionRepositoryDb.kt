package com.example.evcondata.data.consumption

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ConsumptionRepositoryDb(private val databaseManager: DatabaseManager) : ConsumptionRepository {

    override suspend fun getConsumption(id: String): Consumption {
        return withContext(Dispatchers.IO){
            try {
                val db = databaseManager.getConsumptionDatabase()
                db?.let { database ->
                    val doc = database.getDocument(id)
                    doc?.let { document  ->
                        return@withContext Gson().fromJson(document.toJSON(), Consumption::class.java)
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext Consumption(null, null, null,  null,null,null, null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getConsumptionListFlow(): Flow<List<Consumption>> {
        val db = databaseManager.getConsumptionDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(SelectResult.all())
                .from(it.`as`("item")).where(Expression.property("type").equalTo(Expression.string("consumption")))
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToConsumptionList(qc) }
            .flowOn(Dispatchers.IO)

        query.execute()
        return flow
    }

    private fun mapQueryChangeToConsumptionList(queryChange: QueryChange) : List<Consumption>{
        val consumptionList = mutableListOf<Consumption>()
        queryChange.results?.let { results ->
            results.forEach() { result ->
                consumptionList.add(Gson().fromJson(result.toJSON(), ConsumptionModelDTO::class.java).item)
            }
        }
        return consumptionList
    }

    override suspend fun saveConsumption(consumption: Consumption): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try{
                val db = databaseManager.getConsumptionDatabase()
                db?.let { database ->
                    val json = Gson().toJson(consumption)
                    val doc = MutableDocument("Test", json)
                    database.save(doc)
                    result = true
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun deleteConsumption(id: String): Boolean {
        return withContext(Dispatchers.IO){
            var result = false
            try {
                val db = databaseManager.getConsumptionDatabase()
                db?.let { database ->
                    val projectDoc = database.getDocument(id)
                    projectDoc?.let { document ->
                        db.delete(document)
                        result = true
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override fun initializeDatabase() {
        return databaseManager.initializeDatabase()
    }

    override suspend fun deleteDatabase() {
        return withContext(Dispatchers.IO){
            return@withContext databaseManager.deleteDatabase()
        }
    }
}