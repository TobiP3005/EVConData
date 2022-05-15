package com.example.evcondata.data.consumption

import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {

    fun initializeDatabase()

    fun getConsumptionListFlow(): Flow<List<ConsumptionModelDTO>>?

    fun getPublicConsumptionListFlow(): Flow<List<ConsumptionModelDTO>>

    fun getConsumption(id: String): Flow<Consumption?>

    fun saveConsumption(consumption: Consumption, id: String): Flow<ResultCode>

    fun deleteConsumption(id: String) : Flow<ResultCode>

    suspend fun deleteDatabase()
}