package com.example.evcondata.data.consumption

import com.example.evcondata.model.Consumption
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {

    fun initializeDatabase()

    suspend fun getConsumption(id: String): Consumption

    fun getConsumptionListFlow(): Flow<List<Consumption>>?

    suspend fun saveConsumption(consumption: Consumption): Boolean

    suspend fun deleteConsumption(id: String) : Boolean

    suspend fun deleteDatabase()
}