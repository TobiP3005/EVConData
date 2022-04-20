package com.example.evcondata.ui.consumption

import androidx.lifecycle.*
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.model.Consumption
import com.example.evcondata.model.ConsumptionModelDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionViewModel @Inject constructor (private val consumptionRepository: ConsumptionRepository)
    : ViewModel() {

    val consumptionList: Flow<List<ConsumptionModelDTO>>?
        get() = consumptionRepository.getConsumptionListFlow()

    val saveConsumption: (Consumption, String) -> Boolean = { item: Consumption, id: String ->
        var didSave = false
        viewModelScope.launch(Dispatchers.IO){
            didSave = consumptionRepository.saveConsumption(item, id)
        }
        didSave
    }

    val getConsumption: (String) -> Consumption? = { id: String ->
        var item: Consumption? = null
        viewModelScope.launch(Dispatchers.IO){
            item = consumptionRepository.getConsumption(id)
        }
        item
    }

    val deleteConsumption: (String) -> Boolean = { consumptionId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO){
            didDelete = consumptionRepository.deleteConsumption(consumptionId)
        }
        didDelete
    }
}