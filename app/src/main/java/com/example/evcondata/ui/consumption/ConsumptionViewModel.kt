package com.example.evcondata.ui.consumption

import androidx.lifecycle.*
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.model.Consumption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionViewModel @Inject constructor (private val consumptionRepository: ConsumptionRepository)
    : ViewModel() {

    private var _consumptionList: Flow<List<Consumption>> = consumptionRepository.getConsumptionListFlow()!!
    val consumptionList: Flow<List<Consumption>> get() = _consumptionList

    val saveConsumption: (Consumption) -> Boolean = { item: Consumption ->
        var didSave = false
        viewModelScope.launch(Dispatchers.IO){
            didSave = consumptionRepository.saveConsumption(item)
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