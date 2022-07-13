package com.example.evcondata.ui.map

import androidx.lifecycle.*
import com.example.evcondata.data.location.LocationRepository
import com.example.evcondata.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor (
    private val locationRepository: LocationRepository
    )
    : ViewModel() {

    val publishLocation: (Boolean) -> Unit = { isChecked: Boolean ->
        locationRepository.publishLocation(isChecked)
    }

    val locationSharedBool: Boolean
        get() = locationRepository.isLocationShared()

    val locationList: Flow<List<Location>>?
        get() = locationRepository.getLocationListFlow()

    val myLocation: LocationModelDTO?
        get() = locationRepository.getMyLocation()

    val saveLocation: (Location, String) -> Flow<ResultCode> = { item: Location, id: String ->
        locationRepository.saveLocation(item, id)
    }
}