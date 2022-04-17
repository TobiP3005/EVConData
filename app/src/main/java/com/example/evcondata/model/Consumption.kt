package com.example.evcondata.model

interface ModelDTO<T> {
    var item: T
}

class ConsumptionModelDTO(override var item: Consumption) : ModelDTO<Consumption>

data class Consumption (
    var name: String?,
    var distance: Float?,
    var consumption: Float?,
    var temperature: Int?,
    var altitudeUp: Int?,
    var altitudeDown: Int?,
    var type: String = "consumption"
)