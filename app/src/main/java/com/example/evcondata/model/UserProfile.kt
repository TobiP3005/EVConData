package com.example.evcondata.model

data class UserProfile(
    var publicConsumption: Boolean,
    var myCar: String,
    val type: String = "user_profile"
)