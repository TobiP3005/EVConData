package com.example.evcondata.data.car

import com.example.evcondata.model.Car
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getCarListFlow(): Flow<List<Car>>?
}