package com.example.evcondata.ui.consumption

import androidx.lifecycle.*
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import com.example.evcondata.model.ResultCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ConsumptionViewModel @Inject constructor (private val consumptionRepository: ConsumptionRepository)
    : ViewModel() {

    val consumptionList: Flow<List<ConsumptionModelDTO>>?
        get() = consumptionRepository.getConsumptionListFlow()

    val publicConsumptionList: Flow<List<ConsumptionModelDTO>>
        get() = consumptionRepository.getPublicConsumptionListFlow()

    val getConsumption: (String) -> Flow<Consumption?> = { id: String ->
        consumptionRepository.getConsumption(id)
    }

    val saveConsumption: (Consumption, String) -> Flow<ResultCode> = { item: Consumption, id: String ->
            consumptionRepository.saveConsumption(item, id)
    }

    val deleteConsumption: (String) -> Flow<ResultCode> = { consumptionId: String ->
        consumptionRepository.deleteConsumption(consumptionId)
    }

    val updatePublishData: (Boolean) -> Unit = { publishDataBool: Boolean ->
        consumptionRepository.publishData(publishDataBool)
    }

    val sharedConFlow: Flow<String>
        get() = consumptionRepository.sharedConFlow()

}