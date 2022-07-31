package com.example.evcondata.ui.car

import androidx.lifecycle.*
import com.example.evcondata.data.car.CarRepository
import com.example.evcondata.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CarListViewModel @Inject constructor (private val carRepository: CarRepository)
    : ViewModel() {

    val carList: Flow<List<Car>>?
        get() = carRepository.getCarListFlow()
}