package com.example.evcondata.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LocationModelDTO(override var item: Location, var id: String) : ModelDTO<Location>, Parcelable

@Parcelize
data class Location(
    var username: String?,
    var owner: String?,
    var lat: Double,
    var lon: Double,
    var car: String?,
    var published: Boolean? = false,
    var type: String = "location"
) : Parcelable
