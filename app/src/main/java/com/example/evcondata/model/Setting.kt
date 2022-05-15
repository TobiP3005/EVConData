package com.example.evcondata.model

data class Setting(
    var publicConsumption: Boolean,
    val type: String = "user_settings"
)