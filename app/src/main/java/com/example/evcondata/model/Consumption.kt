package com.example.evcondata.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

interface ModelDTO<T> {
    var item: T
}
@Parcelize
class ConsumptionModelDTO(override var item: Consumption, var id: String) : ModelDTO<Consumption>, Parcelable

@Parcelize
data class Consumption (
    var name: String?,
    var datetime: String?,
    var distance: Int?,
    var consumption: Float?,
    var temperature: Int?,
    var altitudeUp: Int?,
    var altitudeDown: Int?,
    var car: String?,
    var username: String? = null,
    var owner: String? = null,
    var public: Boolean? = false,
    var type: String = "consumption"
) : Parcelable