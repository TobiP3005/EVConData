package com.example.evcondata.ui.consumption

import androidx.lifecycle.*
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionViewModel @Inject constructor (private val consumptionRepository: ConsumptionRepository)
    : ViewModel() {

    val consumptionList: Flow<List<ConsumptionModelDTO>>?
        get() = consumptionRepository.getConsumptionListFlow()

    val getConsumption: (String) -> Flow<Consumption?> = { id: String ->
        consumptionRepository.getConsumption(id)
    }

    val saveConsumption: (Consumption, String) -> Flow<ResultCode> = { item: Consumption, id: String ->
            consumptionRepository.saveConsumption(item, id)
    }

    val deleteConsumption: (String) -> Flow<ResultCode> = { consumptionId: String ->
        consumptionRepository.deleteConsumption(consumptionId)
    }
}