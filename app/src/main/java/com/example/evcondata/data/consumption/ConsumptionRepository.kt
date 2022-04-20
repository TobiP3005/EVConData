package com.example.evcondata.data.consumption

import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {

    fun initializeDatabase()

    suspend fun getConsumption(id: String): Consumption

    fun getConsumptionListFlow(): Flow<List<ConsumptionModelDTO>>?

    suspend fun saveConsumption(consumption: Consumption, id: String): Boolean

    suspend fun deleteConsumption(id: String) : Boolean

    suspend fun deleteDatabase()
}