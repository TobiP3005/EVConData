package com.example.evcondata.data.car

import com.couchbase.lite.*
import com.example.evcondata.data.DatabaseManager
import com.example.evcondata.model.Car
import com.example.evcondata.model.CarModelDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CarRepositoryDb(private val databaseManager: DatabaseManager) : CarRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCarListFlow(): Flow<List<Car>> {
        val db = databaseManager.getCarDatabase()
        val query = db?.let { DataSource.database(it) }?.let {
            QueryBuilder.select(
                SelectResult.expression(Meta.id),
                SelectResult.all())
                .from(it.`as`("item")).where(Expression.property("type").equalTo(Expression.string("consumption")))
        }
        val flow = query!!
            .queryChangeFlow()
            .map { qc -> mapQueryChangeToCarList(qc) }
            .flowOn(Dispatchers.IO)

        query.execute()
        return flow
    }

    private fun mapQueryChangeToCarList(queryChange: QueryChange) : List<Car>{
        val carList = mutableListOf<Car>()
        queryChange.results?.let { results ->
            results.forEach() { result ->
                carList.add(Gson().fromJson(result.toJSON(), CarModelDTO::class.java).item)
            }
        }
        return carList
    }
}