package com.example.evcondata.data.consumption

import android.util.Log
import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import com.example.evcondata.model.Setting
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class ConsumptionRepositoryDb(private val databaseManager: DatabaseManager, userPreferencesRepository: UserPreferencesRepository) : ConsumptionRepository {

    private val userPref = userPreferencesRepository

    init {
        setSharedConsumptionPref()
    }

    private fun setSharedConsumptionPref(){
        var shared = "false"
        try{
            val userID = userPref.userId
            val db = databaseManager.getConsumptionDatabase()
            val doc = db?.getDocument("settings:$userID")
            if (doc != null){
                shared = doc.getBoolean("publicConsumption").toString()
            }else {
                val json = Gson().toJson(Setting(false))
                val settingsDoc = MutableDocument("settings:$userID", json)

                db?.save(settingsDoc)
            }
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }

        CoroutineScope(Dispatchers.IO).launch {
            userPref.setSharedConBool(shared)
        }
    }

    override fun sharedConFlow(): Flow<String> {
        return userPref.sharedConFlow
    }

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


    override fun publishData(publishDataBool: Boolean){

        val sharedBool = userPref.sharedConsumption

        if (publishDataBool != sharedBool){
            CoroutineScope(Dispatchers.IO).launch {
                userPref.setSharedConBool(publishDataBool.toString())
            }

            val db = databaseManager.getConsumptionDatabase()

            val json = Gson().toJson(Setting(publishDataBool))

            val docOrgigin = db?.getDocument("settings:"+userPref.userId)

            val doc = MutableDocument(docOrgigin?.id, json)
            db?.save(doc)

            val query = db?.let { DataSource.database(it) }?.let {
                QueryBuilder.select(
                    SelectResult.expression(Meta.id))
                    .from(it.`as`("item"))
                    .where(Expression.property("type").equalTo(Expression.string("consumption"))
                        .and(Expression.property("owner").equalTo(Expression.string(userPref.userId))))
            }
            try {
                val rs = query!!.execute()
                for (result in rs) {
                    val id: String? = result.getString("id")
                    if (id != null){
                        val doc: MutableDocument = db.getDocument(id)!!.toMutable()
                        doc.setBoolean("public", publishDataBool)
                        db.save(doc)
                    }
                }
            } catch (e: CouchbaseLiteException) {
                throw e
            }
        }
    }

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

        return flow
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPublicConsumptionListFlow(): Flow<List<ConsumptionModelDTO>> {
        val db = databaseManager.getConsumptionDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all())
                .from(it.`as`("item"))
                .where(Expression.property("type").equalTo(Expression.string("consumption"))
                    .and(Expression.property("public").equalTo(Expression.booleanValue(true))))
        }

        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToConsumptionList(qc) }
            .flowOn(Dispatchers.IO)

        CoroutineScope(Dispatchers.IO).launch {
            purgeWorkaround()
        }
        return flow
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun purgeWorkaround() {
        val repl = databaseManager.getReplicator()
        val replicatedDocs = repl!!.documentReplicationFlow()
            .map { update -> update.documents }
            .flowOn(Dispatchers.IO)

        replicatedDocs
            .collect { list ->
                val purgeBool = list.any { it.flags.contains(DocumentFlag.ACCESS_REMOVED) }
                if (purgeBool){
                    saveConsumption(Consumption(null, null, null, null, null,null,null,null,null, true), "purgeWorkaround").collect()
                    deleteConsumption("purgeWorkaround").collect()
                }
            }
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
            consumption.username = userPref.username
            consumption.owner = userPref.userId
            consumption.public = userPref.sharedConsumption
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