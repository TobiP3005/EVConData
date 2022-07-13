package com.example.evcondata.data.location

import com.example.evcondata.model.*
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationListFlow(): Flow<List<Location>>?

    fun saveLocation(location: Location, id: String): Flow<ResultCode>

    fun getMyLocation(): LocationModelDTO?

    fun publishLocation(checked: Boolean)

    fun isLocationShared() : Boolean
}