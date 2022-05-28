package com.example.evcondata.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CarModelDTO(override var item: Car, var id: String) : ModelDTO<Car>, Parcelable

@Parcelize
data class Car(
    var name: String?,
    var power: Int?,
    var battery: Double?,
    var wltpConsumption: Double?,
    var wltpRange: Int?,
    var chargeSpeed: Int?,
    var maxSpeed: Int?,
    var acceleration: Float?,
    var imageUri: String?,
    var type: String = "car"
) : Parcelable
