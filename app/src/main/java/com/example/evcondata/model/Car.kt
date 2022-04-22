package com.example.evcondata.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Car(
    var name: String?,
    var power: Int?,
    var battery: Double,
    var wltpConsumption: Double,
    var wltpRange: Int?,
    var chargeSpeed: Int?,
    var maxSpeed: Int?,
    var imageUri: String?,
    var type: String = "car"
) : Parcelable