package com.example.evcondata.data.consumption

import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {

    fun getMyConsumptionListFlow(): Flow<List<ConsumptionModelDTO>>

    fun getPublicConsumptionListFlow(): Flow<List<ConsumptionModelDTO>>

    fun getConsumption(id: String): Flow<Consumption?>

    fun getCommunityAvgConsumption(carName: String): Flow<Float?>

    fun getMyAvgConsumption(carName: String): Flow<Float?>

    fun saveConsumption(consumption: Consumption, id: String): Flow<ResultCode>

    fun publishData(publishDataBool: Boolean)

    fun sharedConFlow(): Flow<String>

    fun deleteConsumption(id: String): Flow<ResultCode>

    suspend fun deleteDatabase()
}