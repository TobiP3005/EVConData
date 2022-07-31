package com.example.evcondata.data.car

import com.example.evcondata.model.Car
import kotlinx.coroutines.flow.Flow

interface CarRepository {

    fun getMyCar(): Car?

    fun getCar(carName: String): Car?

    fun getCarListFlow(): Flow<List<Car>>?

    fun setMyCar(myCar: String)

    fun getCarNames(): Flow<List<String>>
}