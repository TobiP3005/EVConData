package com.example.evcondata.ui.myCar

import androidx.lifecycle.ViewModel
import com.example.evcondata.data.auth.UserPreferencesRepository
import com.example.evcondata.data.car.CarRepository
import com.example.evcondata.data.consumption.ConsumptionRepository
import com.example.evcondata.data.location.LocationRepository
import com.example.evcondata.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MyCarViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val consumptionRepository: ConsumptionRepository,
    private val locationRepository: LocationRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPref = userPreferencesRepository

    val myCar: Car?
        get() = carRepository.getMyCar()

    val getCar: (String) -> Car? = { carName: String ->
        carRepository.getCar(carName)
    }

    val myCarFlow: Flow<String>
        get() = userPref.myCarFlow

    fun setMyCar(carName: String) {
        carRepository.setMyCar(carName)
        locationRepository.updateCar(carName)
    }

    val carNames: Flow<List<String>>
        get() = carRepository.getCarNames()

    val getAvgConsumption: (String) -> Flow<Float?> = { carName: String ->
        consumptionRepository.getCommunityAvgConsumption(carName)
    }

    val getMyConsumption: (String) -> Flow<Float?> = { carName: String ->
        consumptionRepository.getMyAvgConsumption(carName)
    }
}